package com.fit.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisMessageListenerConfig {

    @Bean
    public ReactiveRedisMessageListenerContainer redisMessageListenerContainer(ReactiveRedisConnectionFactory connectionFactory) {
        // Sử dụng ReactiveRedisMessageListenerContainer thay vì RedisMessageListenerContainer cho môi trường WebFlux
        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(connectionFactory);
        return container;
    }

    @Bean
    public ChannelTopic keyExpirationTopic() {
        // Đặt tên topic lắng nghe sự kiện hết hạn của các key trong Redis
        return new ChannelTopic("__keyevent@0__:expired"); // Database 0
    }
}
