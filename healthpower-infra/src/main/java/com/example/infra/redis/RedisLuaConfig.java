package com.example.infra.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class RedisLuaConfig {

    @Bean
    public RedisScript<Long> claimScript() throws IOException {
        String script = Files.readString(ResourceUtils.getFile("classpath:lua/claim.lua").toPath());
        return RedisScript.of(script, Long.class);
    }
}
