package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BitMapTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试pfadd
     */
    @Test
    public void testSetbit() {

        String bitKey = "bitmap:test:k1";
        //set是精确统计，用来对比内存占用大小
        String setKey = "set:test:k1";

        for (int j = 0; j < 1000; j++) {
            Long value = (long) j;
            //给这个bitmap上加数,问题：这个数最大是多少？
            stringRedisTemplate.opsForValue().setBit(bitKey, value.intValue(), true);
            stringRedisTemplate.opsForSet().add(setKey, String.valueOf(value));
        }

        stringRedisTemplate.expire(bitKey, 5, TimeUnit.MINUTES);
        stringRedisTemplate.expire(setKey, 5, TimeUnit.MINUTES);

        //可以对比看一下size差多少
        System.out.println(stringRedisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(bitKey.getBytes());
            }
        }).intValue());
        System.out.println(stringRedisTemplate.opsForSet().size(setKey).intValue());

        //看一下某一个值存不存在
        assertTrue(stringRedisTemplate.opsForValue().getBit(bitKey,500));

    }

    /**
     * 测试bitmap内存用量
     */
    @Test
    public void testMemory() {
        //1k
        stringRedisTemplate.opsForValue().setBit("1,000", 1000, true);
        //1w
        stringRedisTemplate.opsForValue().setBit("10,000", 10000, true);
        //10w
        stringRedisTemplate.opsForValue().setBit("100,000", 100000, true);
        //100w
        stringRedisTemplate.opsForValue().setBit("1,000,000", 1000000, true);
        //1000w
        stringRedisTemplate.opsForValue().setBit("10,000,000", 10000000, true);
        //1yi
        stringRedisTemplate.opsForValue().setBit("100,000,000", 100000000, true);
        //2yi
        stringRedisTemplate.opsForValue().setBit("200,000,000", 200000000, true);

        List<String> keys = Arrays.asList("1,000", "10,000", "100,000", "1,000,000", "10,000,000", "100,000,000", "200,000,000");
        keys.forEach(k -> stringRedisTemplate.expire(k, 5, TimeUnit.MINUTES));
    }




}