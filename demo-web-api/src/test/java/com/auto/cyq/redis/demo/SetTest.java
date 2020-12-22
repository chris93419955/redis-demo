package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SetTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试sadd
     */
    @Test
    public void testSadd() {

        String setKey = "set:test:k1";
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            String value = JacksonHelper.serialize(user);
            //一个一个放进去
            stringRedisTemplate.opsForSet().add(setKey, value);
            users.add(value);
        }

        stringRedisTemplate.expire(setKey, 5, TimeUnit.MINUTES);

        //判断user9是否为集合中的元素
        User user9 = User.builder().id(9).name("name9").desc("desc9").build();
        assertTrue(stringRedisTemplate.opsForSet().isMember(setKey, JacksonHelper.serialize(user9)));

        assertEquals(stringRedisTemplate.opsForSet().size(setKey).intValue(), 10);

        //这里不相等的原因是什么？
        assertNotEquals(stringRedisTemplate.opsForSet().members(setKey).stream().collect(Collectors.toList()), users);

        //添加一个已经有的元素？
        User user2 = User.builder().id(2).name("name2").desc("desc2").build();
        stringRedisTemplate.opsForSet().add(setKey, JacksonHelper.serialize(user2));
        assertEquals(stringRedisTemplate.opsForSet().size(setKey).intValue(), 10);

        //添加一个不重复元素
        User user10 = User.builder().id(10).name("name10").desc("desc10").build();
        stringRedisTemplate.opsForSet().add(setKey, JacksonHelper.serialize(user10));
        assertEquals(stringRedisTemplate.opsForSet().size(setKey).intValue(), 11);

    }

    /**
     * 一些集合操作示例
     */
    @Test
    public void testDiff() {

        String oddKey = "oddKey";
        //100以内奇数
        for (int i = 1; i < 100; i = i + 2) {
            stringRedisTemplate.opsForSet().add(oddKey, String.valueOf(i));
        }

        String evenKey = "evenKey";
        //100以内偶数
        for (int i = 0; i < 100; i = i + 2) {
            stringRedisTemplate.opsForSet().add(evenKey, String.valueOf(i));
        }

        String tripleKey = "tripleKey";
        //100以内偶数
        for (int i = 0; i < 100; i = i + 3) {
            stringRedisTemplate.opsForSet().add(tripleKey, String.valueOf(i));
        }

        //奇数和偶数的交集是空集
        assertTrue(stringRedisTemplate.opsForSet().intersect(oddKey, evenKey).isEmpty());

        System.out.println(stringRedisTemplate.opsForSet().intersect(oddKey, tripleKey).stream().collect(Collectors.toList()));

        // 并集
        assertEquals(stringRedisTemplate.opsForSet().union(oddKey, evenKey).size(), 100);

        //差集
        assertEquals(stringRedisTemplate.opsForSet().difference(oddKey, evenKey), stringRedisTemplate.opsForSet().members(oddKey));
        System.out.println(stringRedisTemplate.opsForSet().difference(oddKey, tripleKey));

        stringRedisTemplate.delete(Arrays.asList(oddKey, evenKey, tripleKey));

    }


}
