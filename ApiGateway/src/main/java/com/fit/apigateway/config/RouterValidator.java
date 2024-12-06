package com.fit.apigateway.config;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class RouterValidator {

    public static final List<Pattern> openApiEndpoints = List.of(
            Pattern.compile("/api/v1/auth/login"),
            Pattern.compile("/api/v1/auth/verify-account"),
//            Pattern.compile("/api/v1/auth/login"),
            Pattern.compile("/api/v1/customers/addCustomer"),
            Pattern.compile("/api/v1/customers/register"),
//            Pattern.compile("/api/v1/auth/get-claims"),
            Pattern.compile("/eureka"),
            //Tour
            Pattern.compile("/api/v1/tours/getById"),
            Pattern.compile("/api/v1/tours/getFilteredTours"),
            Pattern.compile("/api/v1/tours/getToursByIds"),
            Pattern.compile("/api/v1/tours/region"),
            Pattern.compile("/api/v1/tours/by-name"),
            Pattern.compile("/api/v1/tours/by-type"),
            Pattern.compile("/api/v1/tours/region-order-by-price"),
            Pattern.compile("/api/v1/tours/region-order-by-departure-date"),
            Pattern.compile("/api/v1/itineraries/by-tour"),
            Pattern.compile("/api/v1/itineraries/activities/by-itinerary"),
            Pattern.compile("/api/v1/tour-tickets/.*"),
            Pattern.compile("/api/v1/tours/tour-tickets/by-tour/.*"),

            //Payment
            Pattern.compile("/api/v1/booking/redis/.+"),
            Pattern.compile("/api/v1/booking/bookTour"),
            Pattern.compile("/api/v1/booking/verify-booking-tour"),
            Pattern.compile("/api/v1/payments/success")
    );

    public static final List<Pattern> internalApiEndpoints = List.of(
            Pattern.compile("/api/v1/auth/get-claims")
    );

    public static final List<Pattern> adminApiEndpoints = List.of(
            Pattern.compile("/api/v1/auth/change-password"),
            Pattern.compile("/api/v1/tours/*"),
            Pattern.compile("/api/v1/customers/*")
    );

    public static final List<Pattern> customerApiEndpoints = List.of(
            Pattern.compile("/api/v1/auth/change-password"),
            Pattern.compile("/api/v1/customers/by-email"),
            Pattern.compile("/api/v1/customers/.*"),
            //Booking
            Pattern.compile("/api/v1/booking(\\?.*)?"),


            //Tour
            Pattern.compile("/api/v1/tours/recommendations-preferences/*"),
            Pattern.compile("/api/v1/tours/recommendations-interactions/*"),
            Pattern.compile("/api/v1/tours/recommendations-preferences/request"),

            //Recommendation
            Pattern.compile("/api/v1/recommendation/.*"),
            //Payment
            Pattern.compile("/api/v1/payments/process-refund"),
            Pattern.compile("/api/v1/booking/redis/customer/.+")
    );

    private Predicate<ServerHttpRequest> createPredicate(List<Pattern> patterns) {
        return request -> patterns.stream()
                .anyMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());
    }

    public final Predicate<ServerHttpRequest> isSecured = createPredicate(openApiEndpoints).negate();
    public final Predicate<ServerHttpRequest> isInternal = createPredicate(internalApiEndpoints);
    public final Predicate<ServerHttpRequest> adminEndpoints = createPredicate(adminApiEndpoints);
    public final Predicate<ServerHttpRequest> customerEndpoints = createPredicate(customerApiEndpoints);
}

