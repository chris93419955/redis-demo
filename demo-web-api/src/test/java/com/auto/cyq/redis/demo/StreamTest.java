package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StreamTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    StreamMessageListenerContainer container;


    /**
     * 测试生成stream和消费stream
     */
    @Test
    public void testGeoOps() throws InterruptedException {

        String streamKey = "stream:test:k1";
        stringRedisTemplate.delete(streamKey);

        String consumerGroup = "consumers";


        //容器启动
        container.start();

        //创建消费者组
        stringRedisTemplate.opsForStream().createGroup(streamKey, consumerGroup);

        StreamListener<String, MapRecord<String, String, String>> listener1 = message -> {
            System.out.println("listener1 消费：" + message.getId());
            stringRedisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, message.getId());
        };

        StreamListener<String, MapRecord<String, String, String>> listener2 = message -> {
            System.out.println("listener2 消费：" + message.getId());
            stringRedisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, message.getId());
        };

        StreamListener<String, MapRecord<String, String, String>> listener3 = message -> {
            System.out.println("listener3 消费：" + message.getId());
        };

        StreamListener<String, MapRecord<String, String, String>> listener4 = message -> {
            System.out.println("listener4 消费：" + message.getId());
        };

        //分组消费，消费者c1和c2一起消费
        container.receiveAutoAck(Consumer.from(consumerGroup, "c1"), StreamOffset.create(streamKey, ReadOffset.lastConsumed()), listener1);
        container.receiveAutoAck(Consumer.from(consumerGroup, "c2"), StreamOffset.create(streamKey, ReadOffset.lastConsumed()), listener2);

        //广播模式
        container.receive(StreamOffset.create(streamKey, ReadOffset.lastConsumed()), listener3);
        container.receive(StreamOffset.create(streamKey, ReadOffset.lastConsumed()), listener4);

        //往stream里面放10个消息
        for (int i = 0; i < 10; i++) {
            Msg msg = Msg.builder().id(i).content("content" + i).build();
            RecordId recordId = stringRedisTemplate.opsForStream().add(streamKey, Collections.singletonMap("msg", JacksonHelper.serialize(msg)));
            System.out.println("send message : " + recordId.getValue());
        }

        Thread.sleep(50000);
        stringRedisTemplate.expire(streamKey, 5, TimeUnit.MINUTES);

    }

    @TestConfiguration
    static class TestConfig {

        //配置消费
        @Bean
        public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
            // 创建配置对象
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> streamMessageListenerContainerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                    .builder()
                    // 一次性最多拉取多少条消息
                    .batchSize(10)
                    .build();

            // 根据配置对象创建监听容器
            return StreamMessageListenerContainer.create(redisConnectionFactory, streamMessageListenerContainerOptions);
        }


    }


}