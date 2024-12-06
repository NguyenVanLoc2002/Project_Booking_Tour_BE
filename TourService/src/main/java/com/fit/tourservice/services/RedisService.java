package com.fit.tourservice.services;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisService {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public RedisService(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> saveData(String key, Object value) {
        return reactiveRedisTemplate.opsForValue().set(key, value);
    }

    public Mono<Object> getData(String key) {
        return reactiveRedisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> acquireLock(String key) {
        return reactiveRedisTemplate.opsForValue().setIfAbsent(key, true, Duration.ofSeconds(30));
    }

    public Mono<Void> releaseLock(String key) {
        return reactiveRedisTemplate.opsForValue().delete(key).then();
    }
}

