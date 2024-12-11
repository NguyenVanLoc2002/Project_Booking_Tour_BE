package com.fit.notificationservice.service;


import com.fit.notificationservice.dtos.BookingDTO;
import com.fit.notificationservice.dtos.reponse.CustomerResponse;
import com.fit.notificationservice.entity.Email;
import com.fit.notificationservice.utils.JwtUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${spring.mail.username}")
    private String sender;

    public Mono<Void> sendEmailAuthBookingTour(BookingDTO bookingDTO) {
        return Mono.fromCallable(() -> {
                    // Tải mẫu HTML và thay thế nội dung động
                    String htmlContent = loadVerifyBookingTourTemplate();
                    htmlContent = htmlContent.replace("${username}", bookingDTO.getUserName());
                    htmlContent = htmlContent.replace("${email}", bookingDTO.getEmail());
                    htmlContent = htmlContent.replace("${phoneNumber}", bookingDTO.getPhoneNumber());
                    htmlContent = htmlContent.replace("${address}",
                            String.format("%s, %s, %s, %s",
                                    bookingDTO.getAddress(),
                                    bookingDTO.getWard(),
                                    bookingDTO.getDistrict(),
                                    bookingDTO.getCity()));
                    htmlContent = htmlContent.replace("${tourId}", String.valueOf(bookingDTO.getTourId()));
                    htmlContent = htmlContent.replace("${bookingDate}", bookingDTO.getBookingDate().toString());
                    htmlContent = htmlContent.replace("${quantity}", String.valueOf(bookingDTO.getQuantity()));
                    htmlContent = htmlContent.replace("${totalAmount}", String.format("%,.0f VND", bookingDTO.getTotalAmount()*24000));
                    htmlContent = htmlContent.replace("${verificationLink}", createVerificationLinkBookingTour(bookingDTO));

                    // Tạo đối tượng Email và gửi
                    Email emailDetails = new Email(bookingDTO.getEmail(), htmlContent, "Xác nhận đặt tour", "");
                    sendVerifyEmail(emailDetails);
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }


    public Mono<Void> sendEmailVerifyAccount(CustomerResponse customerReponse) {
        return Mono.fromCallable(() -> {
                    String htmlContent = loadVerifyAccountTemplate();
                    htmlContent = htmlContent.replace("${username}", customerReponse.getName());
                    htmlContent = htmlContent.replace("${email}", customerReponse.getEmail());
                    htmlContent = htmlContent.replace("${verificationLink}", createVerificationLinkAccount(customerReponse));

                    // Tạo đối tượng Email và gửi
                    Email emailDetails = new Email(customerReponse.getEmail(), htmlContent, "Booking Confirmation", "");
                    sendVerifyEmail(emailDetails);
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic()) // để thực hiện các tác vụ blocking trên các thread riêng biệt, tránh chặn các thread chính.
                .then();
    }

    public void sendVerifyEmail(Email email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(sender);
            helper.setTo(email.getRecipient());
            helper.setText(email.getMsgBody(), true);
            helper.setSubject(email.getSubject());

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            throw new RuntimeException("Error sending email", e); // Ném ra lỗi để có thể xử lý ở nơi gọi
        }
    }

    public String loadVerifyBookingTourTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/NotificationVerifyBookingTour.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    public String loadVerifyAccountTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/NotificationVerifyAccount.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    public String createVerificationLinkBookingTour(BookingDTO bookingDTO) {
        String token = jwtUtils.generateBookingToken(bookingDTO);
        return "https://travelvietnam.io.vn/api/v1/booking/verify-booking-tour?bookingId=" + token;
    }

    public String createVerificationLinkAccount(CustomerResponse customerResponse) {
        String token = jwtUtils.generateTokenAuth(customerResponse);
        log.info("token: {}", token);
        return "https://travelvietnam.io.vn/api/v1/auth/verify-account?token=" + token;
    }
}
