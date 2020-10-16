package com.auto.cyq.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    @Autowired
    @Qualifier("stringRedisTemplate1")
    StringRedisTemplate stringRedisTemplate1;

    @Autowired
    @Qualifier("stringRedisTemplate2")
    StringRedisTemplate stringRedisTemplate2;

    public void block(String key) {
        try {
            stringRedisTemplate1.opsForList().leftPop(key, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setNoBlock(String key, String value) {
        try {
            stringRedisTemplate1.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void block2(String key) {
        try {
            stringRedisTemplate2.opsForList().leftPop(key, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setNoBlock2(String key, String value) {
        try {
            stringRedisTemplate2.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
