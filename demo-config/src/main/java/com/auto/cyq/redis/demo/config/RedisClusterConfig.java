//package com.auto.cyq.redis.demo.config;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.CollectionUtils;
//import redis.clients.jedis.HostAndPort;
//import redis.clients.jedis.JedisCluster;
//import redis.clients.jedis.JedisPoolConfig;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * @author wbs
// * @date 2020/11/30
// * 这个配置类不写也可以，直接在application.properties里面配置
// */
//@Configuration
//@ConfigurationProperties(prefix = "spring")
//@Data
//@Slf4j
//public class RedisClusterConfig {
//
//    private RedisProperties redis;
//
//    @Bean(value = "jedisCluster")
//    public JedisCluster jedisCluster() {
//
//        if (CollectionUtils.isEmpty(redis.getCluster().getNodes())) {
//            throw new RuntimeException();
//        }
//
//        // 设置redis集群的节点信息
//        Set<HostAndPort> nodes = redis.getCluster().getNodes().stream().map(node -> {
//            String[] nodeInfo = node.split(":");
//            if (nodeInfo.length == 2) {
//                return new HostAndPort(nodeInfo[0], Integer.parseInt(nodeInfo[1]));
//            } else {
//                return new HostAndPort(nodeInfo[0], 6379);
//            }
//        }).collect(Collectors.toSet());
//
//        // 配置连接池
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(redis.getJedis().getPool().getMaxActive());
//        jedisPoolConfig.setMaxIdle(redis.getJedis().getPool().getMaxIdle());
//        jedisPoolConfig.setMinIdle(redis.getJedis().getPool().getMinIdle());
//
//        // 创建jediscluster，传入节点列表和连接池配置
//        JedisCluster cluster = new JedisCluster(nodes, jedisPoolConfig);
//        log.info("finish jedis cluster initailization");
//
//        return cluster;
//
//    }
//}
