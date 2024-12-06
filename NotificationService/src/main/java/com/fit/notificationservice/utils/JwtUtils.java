package com.fit.notificationservice.utils;

import com.fit.notificationservice.dtos.BookingDTO;
import com.fit.notificationservice.dtos.reponse.CustomerResponse;
import com.fit.notificationservice.dtos.request.BookingRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateTokenAuth(CustomerResponse customerResponse) {
        Map<String, Object> claims = new HashMap<>();

        // Đặt các thông tin cần thiết từ CustomerResponse vào trong claims
        claims.put("userId", customerResponse.getUserId());
        claims.put("email", customerResponse.getEmail());
        claims.put("name", customerResponse.getName());
        claims.put("registrationDate", customerResponse.getRegistrationDate());
        claims.put("address", customerResponse.getAddress());
        claims.put("gender", customerResponse.isGender());
        claims.put("dateOfBirth", customerResponse.getDateOfBirth().toString());
        claims.put("phoneNumber", customerResponse.getPhoneNumber());

        // Tạo token với thông tin của customerResponse
        return createToken(claims, customerResponse.getEmail());  // Email sẽ là subject của JWT
    }

    public String generateBookingToken(BookingDTO bookingDTO) {
        Map<String, Object> claims = new HashMap<>();

        // Chỉ thêm bookingId vào claims
        claims.put("bookingId", bookingDTO.getBookingId()); // Đảm bảo bookingId là String

        // Tạo token với bookingId
        return createToken(claims, bookingDTO.getBookingId());  // Sử dụng bookingId làm subject
    }


    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // Email làm subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
