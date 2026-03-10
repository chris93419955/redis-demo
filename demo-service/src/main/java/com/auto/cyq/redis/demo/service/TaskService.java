package com.auto.cyq.redis.demo.service;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskService {

    public String doTask1() {

        String start = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String end = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

        return start + "---------->" + end;

    }


    public String doTask2() {
        String start = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String end = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

        return start + "---------->" + end;

    }

}
