package com.fit.userservice.services;

import com.fit.commonservice.utils.Constant;
import com.fit.userservice.dtos.CustomerDTO;
import com.fit.userservice.dtos.UpdateCustomerDTO;
import com.fit.userservice.event.EventProducer;
import com.fit.userservice.models.Customer;
import com.fit.userservice.models.User;
import com.fit.userservice.repositories.CustomerRepository;
import com.fit.userservice.repositories.UserRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Slf4j
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private Gson gson;

    public Flux<CustomerDTO> getAllCustomers(int page, int size) {
        return customerRepository.findAll()
                .flatMap(customer -> userRepository.findById(customer.getUserId())
                        .map(user -> {
                            CustomerDTO customerDTO = CustomerDTO.convertToDto(customer);
                            customerDTO.setName(user.getName());
                            customerDTO.setEmail(user.getEmail());
                            customerDTO.setRegistrationDate(user.getRegistrationDate());
                            return customerDTO;
                        }))
                .skip((long) (page - 1) * size)
                .take(size)
                .switchIfEmpty(Mono.error(new Exception("Customer list empty!")));
    }

    public Mono<Boolean> checkduplicateEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(customer -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<CustomerDTO> addCustomer(CustomerDTO customerDTO) {
        return checkduplicateEmail(customerDTO.getEmail())
                .flatMap(aBoolean -> {
                    if (Boolean.TRUE.equals(aBoolean)) {
                        log.info("Mail exist: " + customerDTO.getEmail());
                        return Mono.error(new Exception("Customer with email " + customerDTO.getEmail() + " already exists"));
                    } else {
                        return eventProducer.send(Constant.NOTIFICATION_CREATED_USER_TOPIC, String.valueOf(customerDTO.getEmail()), gson.toJson(customerDTO)) // Gửi message đến Kafka topic
                                .doOnSuccess(result -> log.info("Message sent to Kafka: " + result))
                                .then(Mono.just(customerDTO));
                    }
                });
    }

    public Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO) {
        User user = new User();
        user.setName(customerDTO.getName());  // Thay thế full_name bằng name
        user.setEmail(customerDTO.getEmail());
        user.setRegistrationDate(LocalDate.now());

        return userRepository.save(user)
                .flatMap(savedUser -> {
                    Customer customer = CustomerDTO.convertToEntity(customerDTO);
                    customer.setUserId(savedUser.getUserId());
                    log.info("Customer: {}", customer);
                    return customerRepository.save(customer)
                            .map(savedCustomer -> {
                                log.info("savedCustomer: {}", savedCustomer);
                                CustomerDTO dto = CustomerDTO.convertToDto(savedCustomer);
                                dto.setName(savedUser.getName());
                                dto.setEmail(savedUser.getEmail());
                                dto.setRegistrationDate(savedUser.getRegistrationDate());
                                return dto;
                            });
                })
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }

    public Mono<CustomerDTO> getInfoByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user ->
                        customerRepository.findByUserId(user.getUserId())
                                .map(customer -> {
                                    CustomerDTO customerDTO = CustomerDTO.convertToDto(customer);
                                    customerDTO.setName(user.getName());
                                    customerDTO.setEmail(user.getEmail());
                                    customerDTO.setRegistrationDate(user.getRegistrationDate());
                                    return customerDTO;
                                })
                )
                .switchIfEmpty(Mono.error(new Exception("Customer list empty!")));
    }

    public Mono<CustomerDTO> updateCustomer(Long userId, UpdateCustomerDTO updateCustomerDTO) {
        // Tìm người dùng theo userId
        return userRepository.findById(userId)
                .flatMap(user -> {
                    // Cập nhật trường name của user
                    user.setName(updateCustomerDTO.getName());
                    return userRepository.save(user)  // Lưu user đã cập nhật
                            .flatMap(savedUser -> {
                                // Cập nhật thông tin khách hàng
                                return customerRepository.updateCustomer(
                                                userId,
                                                updateCustomerDTO.getAddress(),
                                                updateCustomerDTO.isGender(),
                                                updateCustomerDTO.getDateOfBirth(),
                                                updateCustomerDTO.getPhoneNumber())
                                        .thenReturn(savedUser);  // Tiếp tục với user đã được lưu
                            })
                            .flatMap(savedUser -> customerRepository.findByUserId(userId))  // Tìm lại khách hàng để chuyển đổi
                            .map(CustomerDTO::convertToDto);  // Chuyển đổi thành CustomerDTO
                });
    }

}
