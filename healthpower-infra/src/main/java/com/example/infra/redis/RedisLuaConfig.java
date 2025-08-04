package com.example.infra.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisLuaConfig {

//    @Bean
//    public RedisScript<Long> claimScript() throws IOException {
//        String script = Files.readString(ResourceUtils.getFile("classpath:lua/claim.lua").toPath());
//        return RedisScript.of(script, Long.class);
//    }

    @Bean
    public RedisScript<Boolean> claimScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();

        // 문제의 원인이었던 부분. getFile() 대신 ClassPathResource 사용
        redisScript.setLocation(new ClassPathResource("lua/claim.lua"));

        redisScript.setResultType(Boolean.class);
        return redisScript;
    }
}
