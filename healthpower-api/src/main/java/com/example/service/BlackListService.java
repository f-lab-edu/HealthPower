package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlackListService {

    private static final String BLACKLIST_KEY = "blacklist";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // JWT 토큰을 블랙리스트에 추가
    public void addToBlacklist(String jti) {
        redisTemplate.opsForSet().add(BLACKLIST_KEY, jti);
        // 블랙리스트의 토큰이 만료될 수 있도록 설정(예: 1시간)
        redisTemplate.expire(BLACKLIST_KEY, 1, TimeUnit.HOURS);
    }

    // JWT 토큰이 블랙리스트에 있는지 확인
    public boolean isBlacklisted(String jti) {
        return redisTemplate.opsForSet().isMember(BLACKLIST_KEY, jti);
    }
}
