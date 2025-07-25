package com.example.processor;

import com.example.entity.User;
import com.example.entity.log.DeActivationLog;
import com.example.repository.DeActivationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InActivatedUserProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) throws Exception {
        log.info("비활성화 처리 대상 : id={}, lastLoginAt={}", user.getUserId(), user.getLastLoginAt());
        user.setActivated(false);
        return user;
    }
}
