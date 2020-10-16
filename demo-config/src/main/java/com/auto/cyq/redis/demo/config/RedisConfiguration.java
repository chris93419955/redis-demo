package com.auto.cyq.redis.demo.config;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbs
 * @date 2020/9/24
 */
@Configuration
@ConfigurationProperties(prefix = "myredis")
@Data
public class RedisConfiguration {

    private RedisProperties redis1;
    private RedisProperties redis2;

    @Bean(name = "stringRedisTemplate1")
    @Primary
    public StringRedisTemplate stringRedisTemplate1(@Qualifier("RedisLettuceConnectionFactory1") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisConfig1")
    @Primary
    public RedisSentinelConfiguration redisConfig1() {
        RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        redisConfig.sentinel(redis1.getHost(), redis1.getPort());
        redisConfig.setMaster(redis1.getSentinel().getMaster());
        redisConfig.setPassword(RedisPassword.of(redis1.getPassword()));
        if (redis1.getSentinel().getNodes() != null) {
            List<RedisNode> sentinelNode = new ArrayList<RedisNode>();
            for (String sen : redis1.getSentinel().getNodes()) {
                String[] arr = sen.split(":");
                sentinelNode.add(new RedisNode(arr[0], Integer.parseInt(arr[1])));
            }
            redisConfig.setSentinels(sentinelNode);
        }
        return redisConfig;
    }

    @Bean(name = "redisPool1")
    @Primary
    public GenericObjectPoolConfig redisPool1() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redis1.getLettuce().getPool().getMinIdle());
        config.setMaxIdle(redis1.getLettuce().getPool().getMaxIdle());
        config.setMaxTotal(redis1.getLettuce().getPool().getMaxActive());
        config.setMaxWaitMillis(redis1.getLettuce().getPool().getMaxWait().toMillis());
        return config;
    }

    @Bean(name = "RedisLettuceConnectionFactory1")
    @Primary
    public LettuceConnectionFactory RedisLettuceConnectionFactory1(@Qualifier("redisPool1") GenericObjectPoolConfig config,
                                                                   @Qualifier("redisConfig1") RedisSentinelConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }



    @Bean(name = "stringRedisTemplate2")
    public StringRedisTemplate stringRedisTemplate2(@Qualifier("RedisLettuceConnectionFactory2") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisConfig2")
    public RedisSentinelConfiguration redisConfig2() {
        RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        redisConfig.sentinel(redis2.getHost(), redis2.getPort());
        redisConfig.setMaster(redis2.getSentinel().getMaster());
        redisConfig.setPassword(RedisPassword.of(redis2.getPassword()));
        if (redis2.getSentinel().getNodes() != null) {
            List<RedisNode> sentinelNode = new ArrayList<RedisNode>();
            for (String sen : redis2.getSentinel().getNodes()) {
                String[] arr = sen.split(":");
                sentinelNode.add(new RedisNode(arr[0], Integer.parseInt(arr[1])));
            }
            redisConfig.setSentinels(sentinelNode);
        }
        return redisConfig;
    }

    @Bean(name = "redisPool2")
    public GenericObjectPoolConfig redisPool2() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redis2.getLettuce().getPool().getMinIdle());
        config.setMaxIdle(redis2.getLettuce().getPool().getMaxIdle());
        config.setMaxTotal(redis2.getLettuce().getPool().getMaxActive());
        config.setMaxWaitMillis(redis2.getLettuce().getPool().getMaxWait().toMillis());
        return config;
    }

    @Bean(name = "RedisLettuceConnectionFactory2")
    public LettuceConnectionFactory RedisLettuceConnectionFactory2(@Qualifier("redisPool2") GenericObjectPoolConfig config,
                                                                   @Qualifier("redisConfig2") RedisSentinelConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }



}
