package com.auto.cyq.redis.demo;

import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author wbs
 * @date 2020/12/3
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class QueueTest {

    volatile BlockingQueue queue = new LinkedBlockingQueue();

    /**
     * 举例作为消息队列
     */
    @Test
    public void testMQ() {

        //消费
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        List<Integer> list = new ArrayList<>();
                        Queues.drain(queue, list, 1000, 10, TimeUnit.MILLISECONDS);
                        if (list.size() > 0) dealValue(list);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setName("thread-" + i);
            thread.start();
        }
        Integer item = 0;
        try {
            //生产
            while (true) {
                queue.put(item++);
            }
        } catch (Exception e) {

        }
    }

    private void dealValue(List<Integer> list) throws InterruptedException {
        Thread.currentThread().sleep((long) Math.random());
        log.info("queue.size: {}, size is {} consume from {} to {}", queue.size(), list.size(), list.get(0), list.get(list.size() - 1));
    }


}
