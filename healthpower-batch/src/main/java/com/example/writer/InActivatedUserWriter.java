package com.example.writer;

import com.example.entity.User;
import com.example.entity.log.DeActivationLog;
import com.example.repository.DeActivationLogRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InActivatedUserWriter implements ItemWriter<User> {

    private final DeActivationLogRepository deActivationLogRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends User> chunk) throws Exception {
        for (User user : chunk) {
            DeActivationLog log = new DeActivationLog();
            log.setUserId(user.getUserId());
            log.setDeactivatedAt(LocalDateTime.now());
            log.setReason("장기간 미로그인 유저");
            deActivationLogRepository.save(log);
        }
        deActivationLogRepository.flush();
        log.info("db 저장 완료 : {}" , chunk.getItems());
    }
}
