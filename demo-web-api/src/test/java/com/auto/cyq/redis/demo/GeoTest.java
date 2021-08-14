package com.auto.cyq.redis.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author wbs
 * @date 2020/12/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GeoTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 测试添加坐标
     */
    @Test
    public void testGeoOps() {

        String geoKey = "geo:test:k1";

        //添加四个坐标
        //lon:116.167526,lan:39.767806 大鸭梨烤鸭店(长阳店)
        stringRedisTemplate.opsForGeo().add(geoKey, new Point(116.167526, 39.767806), "大鸭梨烤鸭店(长阳店)");

        stringRedisTemplate.opsForGeo().add(geoKey, new Point(116.2785209, 40.0493245), "百度科技园-3号楼");

        stringRedisTemplate.opsForGeo().add(geoKey, new Point(116.318432, 39.986112), "汽车之家");

        stringRedisTemplate.opsForGeo().add(geoKey, new Point(116.318894, 39.985751), "中国农业银行(中关村支行)");


        //获取点的坐标
        stringRedisTemplate.opsForGeo().position(geoKey, "汽车之家", "中国农业银行(中关村支行)").forEach(System.out::println);

        System.out.println("==================两点之间的距离========----===============");
        //计算其中两个点的距离
        System.out.println(stringRedisTemplate.opsForGeo().distance(geoKey, "大鸭梨烤鸭店(长阳店)", "汽车之家"));
        System.out.println(stringRedisTemplate.opsForGeo().distance(geoKey, "中国农业银行(中关村支行)", "汽车之家"));
        System.out.println(stringRedisTemplate.opsForGeo().distance(geoKey, "百度科技园-3号楼", "汽车之家"));

        //获取一定范围内的点
        System.out.println("=============根据点和半径取范围内的点=======================");
        //离汽车之家1公里内的点
        stringRedisTemplate.opsForGeo().radius(geoKey, "汽车之家", 1000).forEach(e -> System.out.println(e.getContent().getName()));

        Metric metric = RedisGeoCommands.DistanceUnit.KILOMETERS;
        Distance distance = new Distance(10, metric);
        Circle circle = new Circle(new Point(116.318432, 39.986112), distance);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(1000);
        //离汽车之家10公里内的点
        stringRedisTemplate.opsForGeo().radius(geoKey, circle, args).forEach(e -> {
            System.out.println(e.getContent().getName() + e.getDistance());
        });

        stringRedisTemplate.expire(geoKey, 5, TimeUnit.MINUTES);


    }


}