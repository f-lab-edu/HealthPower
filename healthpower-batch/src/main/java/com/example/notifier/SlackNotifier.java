/*
package com.example.HealthPower.etc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SlackNotifier {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private final WebClient webClient = WebClient.builder().build();

    public void sendAlert(String title, String status, String message, LocalDateTime checkedAt) {

        String formattedTime = checkedAt.toString();

        String text = String.format(
                "* 서버 상태 알림: %s*\n*상태:* %s\n*메시지:* %s\n*시간:* %s",
                title, status, message, formattedTime
        );

        webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of("text", text))
                .retrieve()
                .toBodilessEntity()
                .subscribe(); //비동기 전송
    }
}
*/
