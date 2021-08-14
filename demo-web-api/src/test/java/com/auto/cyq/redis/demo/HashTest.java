package com.auto.cyq.redis.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HashTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试hget，hset
     */
    @Test
    public void testHgetHset() {

        User user = User.builder().id(1000).name("name1000").desc("desc1000").build();
        String hashKey = "json:test:k1";
        stringRedisTemplate.opsForHash().put(hashKey, String.valueOf(user.id), JacksonHelper.serialize(user));

        //整个hash的大Key一起过期
        stringRedisTemplate.expire(hashKey, 5, TimeUnit.MINUTES);

        assertEquals(stringRedisTemplate.opsForHash().get(hashKey, String.valueOf(user.id)), JacksonHelper.serialize(user));

    }

    @Test
    public void testGet(){
        Object json = stringRedisTemplate.opsForHash().get("a", "b");
        assertEquals(json, null);
    }

    /**
     * 批量放值hmset,hmget
     */
    @Test
    public void testHmset() {
        Map<Object, String> map = new HashMap();

        String hashKey = "hash:test:key";
        for (int i = 1; i < 11; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            map.put(String.valueOf(i), JacksonHelper.serialize(user));
        }
        stringRedisTemplate.opsForHash().putAll(hashKey, map);

        stringRedisTemplate.expire(hashKey, 5, TimeUnit.MINUTES);

        //将得到的两个list对比
        assertEquals(stringRedisTemplate.opsForHash().multiGet(hashKey, map.keySet()), map.values().stream().collect(Collectors.toList()));

    }

    /**
     * 对比内存占用情况
     */
    @Test
    public void testUsage() {

        List<User> list = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            list.add(user);
            //对比用string一个一个放进去
            stringRedisTemplate.opsForValue().set(String.valueOf(i), JacksonHelper.serialize(user), 5, TimeUnit.MINUTES);
        }

        String hashKey = "json:test:k1";
        //对比string一起放进去
        stringRedisTemplate.opsForValue().set("strlist" + hashKey, JacksonHelper.serialize(list), 5, TimeUnit.MINUTES);

    }

    /**
     * 对比使用string操作json和hash操作json
     */
    @Test
    public void testUpdate() {

        Map<String, User> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();

        for (int i = 1; i < 11; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            map.put(String.valueOf(i), user);
            map1.put(String.valueOf(i), JacksonHelper.serialize(user));
        }

        String hashKey = "json:test:users";
        stringRedisTemplate.opsForValue().set("str" + hashKey, JacksonHelper.serialize(map), 5, TimeUnit.MINUTES);
        stringRedisTemplate.opsForHash().putAll("map" + hashKey, map1);

        //update string
        Map<String, User> users = JacksonHelper.deserialize(stringRedisTemplate.opsForValue().get("str" + hashKey), new TypeReference<Map<String, User>>() {
        });
        users.get("1").setSex("male");
        stringRedisTemplate.opsForValue().set("str" + hashKey, JacksonHelper.serialize(users), 5, TimeUnit.MINUTES);

        //update hash
        User user = JacksonHelper.deserialize((String) stringRedisTemplate.opsForHash().get("map" + hashKey, "1"), User.class);
        stringRedisTemplate.opsForHash().put("map" + hashKey, "1", JacksonHelper.serialize(user.setSex("male")));

        stringRedisTemplate.expire("map" + hashKey, 5, TimeUnit.MINUTES);

    }

    /**
     * hgetall
     */
    @Test
    public void testHgetall() {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < 11; i++) {
            User user = User.builder().id(i).name("name" + i).desc("desc" + i).build();
            map.put(String.valueOf(i), JacksonHelper.serialize(user));
        }
        String hashKey = "test:users";
        stringRedisTemplate.opsForHash().putAll(hashKey, map);
        stringRedisTemplate.expire(hashKey, 5, TimeUnit.MINUTES);

        // 这里如果size特别大，会非常慢造成性能下降
        assertEquals(stringRedisTemplate.opsForHash().entries(hashKey), map);
    }


    /**
     * hexist
     */
    @Test
    public void testHexist() {
        stringRedisTemplate.opsForHash().put("testKey", "testHashKey", "1");
        stringRedisTemplate.expire("testKey", 5, TimeUnit.MINUTES);
        assertTrue(stringRedisTemplate.opsForHash().hasKey("testKey", "testHashKey"));
        assertFalse(stringRedisTemplate.opsForHash().hasKey("testKey", "test100"));
        assertFalse(stringRedisTemplate.opsForHash().hasKey("test222", "testHashKey"));
    }


}
