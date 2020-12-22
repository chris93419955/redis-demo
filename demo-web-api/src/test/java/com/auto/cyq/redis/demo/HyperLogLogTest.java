package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HyperLogLogTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试pfadd
     */
    @Test
    public void testPFadd() throws InterruptedException {

        String hllKey = "hll:test:k1";
        //set是精确统计，用来对比精确度
        String setKey = "set:test:k1";
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    String value = String.valueOf(Math.random());
                    //给这个key上加数
                    stringRedisTemplate.opsForHyperLogLog().add(hllKey, value);
                    stringRedisTemplate.opsForSet().add(setKey, value);
                }
                latch.countDown();
            });
            thread.setName("thread-" + i);
            thread.start();

        }
        latch.await();

        stringRedisTemplate.expire(hllKey, 5, TimeUnit.MINUTES);
        stringRedisTemplate.expire(setKey, 5, TimeUnit.MINUTES);

        //可以对比看一下size差多少
        System.out.println(stringRedisTemplate.opsForHyperLogLog().size(hllKey).intValue());
        System.out.println(stringRedisTemplate.opsForSet().size(setKey).intValue());

    }





}