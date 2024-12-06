package com.fit.authservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventConsumer {
//    @Autowired
//    private AuthService authService;
//
////    @Autowired
////    private EventProducer eventProducer;
//
//    private final KafkaReceiver<String, String> kafkaReceiver;
//
//    @Autowired
//    private Gson gson;
//
//    @Autowired
//    public EventConsumer(ReceiverOptions<String, String> options) {
//        log.info("userOnboarding");
//        this.kafkaReceiver = KafkaReceiver.create(options.subscription(Collections.singleton(Constant.USER_ONBOARDING_TOPIC)));
//        this.kafkaReceiver.receive().subscribe(this::userOnboarding);
//    }
//
//    public void userOnboarding(ReceiverRecord<String, String> receiverRecord) {
//        log.info("Customer Onboarding event");
//        CustomerDTO customerDTO = gson.fromJson(receiverRecord.value(), CustomerDTO.class);
//        log.info(customerDTO.toString());
//        AuthUserDTO authUserDTO = new AuthUserDTO();
//        authUserDTO.setEmail(customerDTO.getEmail());
//        authUserDTO.setPassword("123456");
//        authUserDTO.setRole(Role.USER);
//        log.info(authUserDTO.toString());
//        authService.createAuthUser(authUserDTO)
//                .subscribe();
//    }
}
