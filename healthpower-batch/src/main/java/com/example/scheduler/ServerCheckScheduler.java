/*
package com.example.HealthPower.etc;

import com.example.HealthPower.service.ServerCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerCheckScheduler {

    private final WebClient webClient;
    private final SlackNotifier slackNotifier;
    private final ServerCheckService serverCheckService;

    @Scheduled(fixedDelay = 30000) //5분마다
    public void check() {
        webClient.get()
                .uri("/actuator/health")
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> {
                    String body = response.getBody();
                    serverCheckService.saveLog("UP", body);
                    slackNotifier.sendAlert("서버 정상 작동", "UP", body, LocalDateTime.now());
                    log.info("서버 상태 양호 - {}", body);
                })
                .doOnError(ex -> {
                    String msg = "서버 점검 실패 : " + ex.getMessage();
                    serverCheckService.saveLog("DOWN", msg);
                    slackNotifier.sendAlert("서버 상태 이상", "DOWN", msg, LocalDateTime.now());
                    log.error(msg);
                })
                .subscribe();
    }
}
*/
