package com.fit.tourservice.events;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@Slf4j
public class EventProducer {
    @Autowired
    private KafkaSender<String, String> kafkaSender;

    public Mono<String> send(String topic, String key,String message) {
        return kafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<>(topic,key,message),message)))
                .then()
                .thenReturn("OK");
    }
}
