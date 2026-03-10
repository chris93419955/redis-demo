package com.auto.cyq.redis.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.date.DateUtil;
import com.auto.cyq.redis.demo.service.RedisService;
import com.auto.cyq.redis.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wbs
 * @date 2020/9/2
 */
@RestController
public class DemoCtroller {

    @Autowired
    private RedisService redisService;

    @Autowired
    private TaskService taskService;

    @GetMapping("testBlock")
    public String setNoBlock() {
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {
                redisService.block("k");
            });
            thread.setName("setValueToRedis-" + (i + 1));
            thread.start();

        }

        return "ok";
    }

    @GetMapping("testNoBlock")
    public String blockList() {
        for (int i = 0; i < 1; i++) {
            Thread thread = new Thread(() -> {
                redisService.setNoBlock("k", "v");
            });
            thread.setName("setValueToRedis-" + (i + 1));
            thread.start();
        }

        return "ok";
    }

    @GetMapping("test")
    public String dotest() {

        Map<String, CompletableFuture> resultMap = new HashMap<>();
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return taskService.doTask1();
            } catch (Exception e) {
                return null;
            }
        });
        resultMap.put("future1", future1);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return taskService.doTask2();
            } catch (Exception e) {
                return null;
            }
        });
        resultMap.put("future2", future2);

        try {
            String start = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            Thread.sleep(2000);
            String end = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            System.out.println("main------>>" + start + "----------->>" + end);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String str1 = resultMap.get("future1").get().toString();
            System.out.println("str1-->>" + str1);
            String str2 = resultMap.get("future2").get().toString();
            System.out.println("str2-->>" + str2);

        } catch (Exception e) {

        }


        return "ok";
    }


}
