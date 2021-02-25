package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ListTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试Lpush, Rpush
     */
    @Test
    public void testRpush() {

        String listKey = "json:test:k1";
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
//            //一个一个放进去
//            stringRedisTemplate.opsForList().rightPush(listKey, JacksonHelper.serialize(user));
            users.add(JacksonHelper.serialize(user));
        }
        // 一次性放一个list进去
        stringRedisTemplate.opsForList().rightPushAll(listKey, users);

        stringRedisTemplate.expire(listKey, 5, TimeUnit.MINUTES);

        //双向链表
        User user9 = User.builder().id(9).name("name9").desc("desc9").build();
        assertEquals(stringRedisTemplate.opsForList().rightPop(listKey), JacksonHelper.serialize(user9));

        User user0 = User.builder().id(0).name("name0").desc("desc0").build();
        assertEquals(stringRedisTemplate.opsForList().leftPop(listKey), JacksonHelper.serialize(user0));

    }

    /**
     * 获取一个元素，或者一段元素
     */
    @Test
    public void testLRange() {

        String listKey = "range:test:key";
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            users.add(JacksonHelper.serialize(user));
        }
        stringRedisTemplate.opsForList().rightPushAll(listKey, users);

        //取一段，时间复杂度O(n), 由于采用双向链表，所以中间元素的相关操作都是O(n)
        assertEquals(stringRedisTemplate.opsForList().range(listKey, 5, 10), users.subList(5, 11));

        //如果range超过范围，只取到队尾，不会阻塞
        assertEquals(stringRedisTemplate.opsForList().range(listKey, 5, 100), users.subList(5, 20));

        //lpop,rpop是阻塞命令，如果没有元素会阻塞一直等待，这里注意一下
        stringRedisTemplate.opsForList().leftPop("emptylist", 4, TimeUnit.SECONDS);

        //用完之后删除，不影响其他测试
        stringRedisTemplate.delete(listKey);

    }

    /**
     * linsert
     */
    @Test
    public void testLinsert() {
        String listKey = "insert:test:key";
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            users.add(JacksonHelper.serialize(user));
        }
        stringRedisTemplate.opsForList().rightPushAll(listKey, users);

        assertEquals(stringRedisTemplate.opsForList().rightPush(listKey, users.get(1), "testvalue").intValue(), users.size() + 1);
        assertEquals(stringRedisTemplate.opsForList().size(listKey).intValue(), users.size() + 1);
        stringRedisTemplate.expire(listKey, 2, TimeUnit.MINUTES);

    }

    /**
     * 举例作为消息队列
     */
    @Test
    public void testMQ() {
        String listKey = "mq:test:key";
        //生产
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 100; j++)
                    stringRedisTemplate.opsForList().rightPush(listKey, Thread.currentThread().getName() + "," + j);
            });
            thread.setName("thread-" + i);
            thread.start();
        }
        //消费
        while (true) {
            String ele = stringRedisTemplate.opsForList().leftPop(listKey, 4, TimeUnit.SECONDS);
            System.out.println(ele);
            if (null == ele) {
                break;
            }

        }
    }


}
