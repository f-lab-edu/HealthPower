package com.example.infra.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisLuaConfig {

    /*@Bean
    public RedisScript<Boolean> claimScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("lua/claim.lua"));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }*/

    @Bean
    public RedisScript<Long> claimScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("lua/claim.lua"));
        redisScript.setResultType(Long.class); // <-- Change this from Boolean.class to Long.class
        return redisScript;
    }
}
