package com.example.infra.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Configuration
public class RedisLuaConfig {

    @Bean
    public RedisScript<Long> claimScript() throws IOException {
//        String script = Files.readString(ResourceUtils.getFile("classpath:lua/claim.lua").toPath());
//        return RedisScript.of(script, Long.class);

        String script = new String(Files.readAllBytes(new ClassPathResource("lua/claim.lua").getFile().toPath()), StandardCharsets.UTF_8);

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
