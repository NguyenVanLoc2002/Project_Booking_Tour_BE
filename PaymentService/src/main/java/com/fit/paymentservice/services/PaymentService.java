package com.fit.paymentservice.services;

import com.fit.paymentservice.dtos.PaymentDTO;
import com.fit.paymentservice.dtos.request.PaymentRequest;
import com.fit.paymentservice.enums.Currency;
import com.fit.paymentservice.enums.PaymentMethod;
import com.fit.paymentservice.enums.PaymentStatus;
import com.fit.paymentservice.models.Payment;
import com.fit.paymentservice.repositories.PaymentRepository;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private APIContext apiContext;

    @Autowired
    private PaymentRepository paymentRepository;

    public Mono<PaymentDTO> addPayment(PaymentRequest payment) {
        String paymentId = UUID.randomUUID().toString();
        return paymentRepository.insertPayment(
                        paymentId,  // Sử dụng paymentId mới được tạo
                        payment.getBookingId(),
                        payment.getTransactionId(),
                        payment.getDiscountId(),
                        payment.getAmount(),
                        PaymentMethod.PAYPAL.name(),  // Chuyển đổi PaymentMethod thành String
                        PaymentStatus.COMPLETED.name(),   // Chuyển đổi PaymentStatus thành String
                        LocalDate.now(),
                        Currency.USD.name(),         // Chuyển đổi Currency thành String
                        payment.getPaymentId(),
                        0,
                        LocalDate.now(),
                        LocalDate.now()
                )
                .then(paymentRepository.findById(paymentId))  // Tìm Payment vừa chèn
                .map(PaymentDTO::convertToDTO); // Tạo PaymentDTO từ Payment
    }




}
