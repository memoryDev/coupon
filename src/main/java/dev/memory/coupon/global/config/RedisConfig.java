package dev.memory.coupon.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {


    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379");

        return Redisson.create(config);
    }
}
