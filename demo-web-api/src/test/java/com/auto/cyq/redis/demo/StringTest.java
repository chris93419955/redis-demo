package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ClusterSlotHashUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StringTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 测试get，set
     */
    @Test
    public void testGetSet() {

        stringRedisTemplate.opsForValue().set("str:test:k1", "1234567890", 5, TimeUnit.MINUTES);
        assertEquals(stringRedisTemplate.opsForValue().get("str:test:k1"), "1234567890");

        User user = User.builder().id(1000).name("name1000").desc("desc1000").build();

        redisTemplate.opsForValue().set("obj:user:id1", user, 5, TimeUnit.MINUTES);
        assertEquals(redisTemplate.opsForValue().get("obj:user:id1"), user);

        //可以看出jackson序列化之后的字符串比较用户友好，而且占用空间少
        stringRedisTemplate.opsForValue().set("str:user:id1", JacksonHelper.serialize(user), 5, TimeUnit.MINUTES);
        assertEquals(stringRedisTemplate.opsForValue().get("str:user:id1"), JacksonHelper.serialize(user));
        assertEquals(JacksonHelper.deserialize(stringRedisTemplate.opsForValue().get("str:user:id1"), User.class), user);
        assertNotEquals(redisTemplate.opsForValue().get("str:user:id1"), stringRedisTemplate.opsForValue().get("str:user:id1"));

        //序列化换成这样，这两就一样了
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(User.class));
        redisTemplate.opsForValue().set("strobj:user:id1", user, 5, TimeUnit.MINUTES);

    }

    /**
     * 测试mget，mset，在集群模式或codis下会出现的情况
     */
    @Test
    public void testMsetMget() {

        assertEquals(ClusterSlotHashUtil.calculateSlot("a"), 15495);
        assertEquals(ClusterSlotHashUtil.calculateSlot("b"), 3300);
        assertEquals(ClusterSlotHashUtil.calculateSlot("c"), 7365);

        Map<String, String> m = new HashMap();
        m.put("a", "a");
        m.put("b", "b");
        m.put("c", "c");

        stringRedisTemplate.opsForValue().multiSet(m);

        //可以看出虽然是一个命令，但是在lettuce执行的时候会有拆分 分发耗时+多个redis server耗时+多个网络RT+合并耗时
        assertEquals(stringRedisTemplate.opsForValue().multiGet(m.keySet()), m.values().stream().collect(Collectors.toList()));

        m.keySet().forEach(k -> stringRedisTemplate.expire(k, 5, TimeUnit.MINUTES));

        assertEquals(ClusterSlotHashUtil.calculateSlot("{hashtag}:a"), 13784);
        assertEquals(ClusterSlotHashUtil.calculateSlot("{hashtag}:b"), 13784);
        assertEquals(ClusterSlotHashUtil.calculateSlot("{hashtag}:c"), 13784);

        Map<String, String> hm = new HashMap();
        String hashTag = "{hashtag}:";
        hm.put(hashTag + "a", "a");
        hm.put(hashTag + "b", "b");
        hm.put(hashTag + "c", "c");

        stringRedisTemplate.opsForValue().multiSet(hm);
        assertEquals(stringRedisTemplate.opsForValue().multiGet(hm.keySet()), hm.values().stream().collect(Collectors.toList()));

        hm.keySet().forEach(k -> stringRedisTemplate.expire(k, 5, TimeUnit.MINUTES));

    }

    /**
     * 测试incr，decr
     */
    @Test
    public void testIncrDecr() {
        long i = 1L;
        assertEquals(Long.parseLong(String.valueOf(stringRedisTemplate.opsForValue().increment("numberkey"))), i);
        assertEquals(Long.parseLong(String.valueOf(stringRedisTemplate.opsForValue().decrement("numberkey"))), 0);
        stringRedisTemplate.expire("numberkey", 5, TimeUnit.MINUTES);
    }

}
