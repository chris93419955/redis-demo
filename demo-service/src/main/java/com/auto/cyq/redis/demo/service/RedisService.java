package com.auto.cyq.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    JedisCluster jedisCluster;

    public void block(String key) {
        try {
            stringRedisTemplate.opsForList().leftPop(key, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setNoBlock(String key, String value) {
        try {
//            jedisCluster.set(key, value);
            stringRedisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
