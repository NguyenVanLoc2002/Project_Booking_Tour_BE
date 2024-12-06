package com.fit.userservice.controllers;

import com.fit.commonservice.utils.CommonFunction;
import com.fit.userservice.dtos.CustomerDTO;
import com.fit.userservice.dtos.UpdateCustomerDTO;
import com.fit.userservice.services.CustomerService;
import com.fit.userservice.utils.Constant;
import com.fit.userservice.utils.JwtUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping
    public Mono<ResponseEntity<Flux<CustomerDTO>>> getAllCustomers(@RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(customerService.getAllCustomers(page, size))) // Tạo ResponseEntity từ Flux
                .onErrorResume(e -> {
                    log.error("Error fetching customers: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); // Trả về 500 nếu có lỗi
                });
    }



    @GetMapping("/by-email")
    public Mono<ResponseEntity<CustomerDTO>> getCustomerByEmail(@RequestHeader("Authorization") String authorization) {
        log.info("Fetching user details from token");

        //Kiem tra token
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        String token = authorization.substring(7);
        return Mono.just(token)
                .flatMap(t -> {
                    log.info("Fetching token: {}", t);
                    String emailFromToken = jwtUtils.extractUsername(token);
                    return customerService.getInfoByEmail(emailFromToken)
                            .map(customerDTO -> ResponseEntity.ok(customerDTO))
                            .defaultIfEmpty(ResponseEntity.notFound().build());
                })
                .onErrorResume(e -> {
                    log.error("Error fetching user details: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping(value = "/checkduplicate/{email}")
    public Mono<ResponseEntity<Boolean>> checkDuplicate(@PathVariable String email) {
        return customerService.checkduplicateEmail(email)
                .map(duplicate -> ResponseEntity.ok(duplicate)) // Chuyển đổi kết quả thành ResponseEntity
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Nếu không tìm thấy, trả về 404
    }


    @PostMapping(value = "/addCustomer")
    public Mono<ResponseEntity<CustomerDTO>> addCustomer(@RequestBody String requestStr) {
        log.info("Add customer: {}", requestStr);
        InputStream inputStream = CustomerController.class.getClassLoader().getResourceAsStream(Constant.JSON_REQ_CREATE_CUSTOMER);

        // Validate JSON
        CommonFunction.jsonValidate(inputStream, requestStr);

        // Thêm customer và trả về ResponseEntity
        return customerService.addCustomer(gson.fromJson(requestStr, CustomerDTO.class))
                .map(customerDTO -> ResponseEntity.status(HttpStatus.CREATED).body(customerDTO)) // Trả về 201 với CustomerDTO
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()); // Trả về 400 nếu có lỗi
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<CustomerDTO>> createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO)
                .map(savedCustomerDTO -> ResponseEntity.ok(savedCustomerDTO)) // Trả về phản hồi 200 OK cùng với đối tượng CustomerDTO đã được lưu
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()) // Trả về phản hồi 400 nếu không có dữ liệu
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); // Trả về phản hồi 500 nếu có lỗi xảy ra
    }


    @PutMapping("/{userId}")
    public Mono<ResponseEntity<CustomerDTO>> updateCustomer(
            @PathVariable Long userId,
            @RequestBody UpdateCustomerDTO updateCustomerDTO) {

        return customerService.updateCustomer(userId, updateCustomerDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")))  // Nếu không tìm thấy customer thì trả về lỗi
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)));  // Trả về HTTP 404 nếu gặp lỗi
    }

}
