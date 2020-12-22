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
public class ZsetTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试zadd
     */
    @Test
    public void testZadd() {

        String zsetKey = "zset:test:k1";
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            String value = JacksonHelper.serialize(user);
            //一个一个放进去
            stringRedisTemplate.opsForZSet().add(zsetKey, value, i);
            users.add(value);
        }

        stringRedisTemplate.expire(zsetKey, 5, TimeUnit.MINUTES);

        assertEquals(stringRedisTemplate.opsForZSet().size(zsetKey).intValue(), 10);

        //对比一下set，这里为什么相等？
        assertEquals(stringRedisTemplate.opsForZSet().range(zsetKey, 0, 9).stream().collect(Collectors.toList()), users);


    }

    /**
     * 一些排序操作示例
     */
    @Test
    public void testSort() {
        //先初始化100个user
        String sortKey = "sort:test:k1";
        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<ZSetOperations.TypedTuple<String>>();

        for (int i = 0; i < 100; i++) {
            ZSetOperations.TypedTuple<String> ele = new DefaultTypedTuple<String>(String.valueOf(i), 0.0);
            set.add(ele);
        }
        stringRedisTemplate.opsForZSet().add(sortKey, set);

        //给user点赞，或者权重，分数等场景
        stringRedisTemplate.opsForZSet().incrementScore(sortKey, String.valueOf(21), 1);
        stringRedisTemplate.opsForZSet().incrementScore(sortKey, String.valueOf(15), 2);
        stringRedisTemplate.opsForZSet().incrementScore(sortKey, String.valueOf(17), 4);
        stringRedisTemplate.opsForZSet().incrementScore(sortKey, String.valueOf(5), 2);

        System.out.println(stringRedisTemplate.opsForZSet().rank(sortKey, JacksonHelper.serialize(User.builder().id(7).build())));

        //从多到少输出收到点赞的用户
        System.out.println(stringRedisTemplate.opsForZSet().reverseRange(sortKey, 0, 4).stream().collect(Collectors.toList()));

        stringRedisTemplate.expire(sortKey, 5, TimeUnit.MINUTES);

    }

    /**
     * score作为最近更新时间，按照时间排序
     */
    @Test
    public void testSort2() throws InterruptedException {
        String sortKey = "sort:test:k2";
        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<ZSetOperations.TypedTuple<String>>();

        for (int i = 0; i < 100; i++) {
            ZSetOperations.TypedTuple<String> ele = new DefaultTypedTuple<String>(String.valueOf(i), Double.valueOf(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()))));
            set.add(ele);
        }
        stringRedisTemplate.opsForZSet().add(sortKey, set);
        //10个线程来更新
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    String val = String.valueOf((int) (Math.random() * 100));
                    //每个线程随机选几个val，来增加val
                    stringRedisTemplate.opsForZSet().incrementScore(sortKey, val, Double.parseDouble(val));
                }
                latch.countDown();
            });
            thread.setName("thread-" + i);
            thread.start();
        }
        latch.await();
        //都执行完成后由大到小输出几个
        stringRedisTemplate.opsForZSet().reverseRangeWithScores(sortKey, 0, 4).stream().forEach(e -> System.out.println(e.getValue() + "," + BigDecimal.valueOf(e.getScore()).toPlainString()));
        stringRedisTemplate.expire(sortKey, 5, TimeUnit.MINUTES);

    }

    /**
     * Zset数据结构占用内存情况
     */
    @Test
    public void testUsage() {
        String usageKey = "usage:test:k1";
        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<ZSetOperations.TypedTuple<String>>();
        for (int i = 0; i < 100000; i++) {
            ZSetOperations.TypedTuple<String> ele = new DefaultTypedTuple<String>(String.valueOf(i), 1000.0);
            set.add(ele);
        }
        //十万条数据usage是10M
        stringRedisTemplate.opsForZSet().add(usageKey, set);

    }


}