package com.example.guardrailService.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class NotificationScheduler {
    private final StringRedisTemplate redisTemplate;

    @Scheduled(fixedRate = 300000)
    public void processNotifications() {
        System.out.println("Running notification");

        Set<String> users = redisTemplate.opsForSet()
                .members("users_with_notifications");

        if (users == null || users.isEmpty()) {
            System.out.println("No pending notifications.");
            return;
        }

        for (String userId : users) {
            String listKey = "user:" + userId + ":pending_notifs";
            Long size = redisTemplate.opsForList().size(listKey);

            if (size == null || size == 0) {
                continue;
            }
            var messages = redisTemplate.opsForList()
                    .range(listKey, 0, -1);

            if (messages != null && !messages.isEmpty()) {
                System.out.println("Summarized Notification for User " + userId + ": " + messages.get(0) + " and " + (size - 1) + " others interacted with your posts.");
            }

            redisTemplate.delete(listKey);
        }
        redisTemplate.delete("users_with_notifications");
        System.out.println("Notification completed.");
    }
}
