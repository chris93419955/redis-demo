package com.auto.cyq.redis.demo.controller;

import com.auto.cyq.redis.demo.service.RedisService;
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
                redisService.setNoBlock2("k2","v2");
            });
            thread.setName("setValueToRedis-" + (i + 1));
            thread.start();
        }

        return "ok";
    }

}
