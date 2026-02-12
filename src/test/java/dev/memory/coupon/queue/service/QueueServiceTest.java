package dev.memory.coupon.queue.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Test
    @DisplayName("150명이 대기열에 진입한다.")
    void joinQueueTest() {
        for (int i = 0; i < 150; i++) {
            queueService.joinQueue(i + 1L);
        }

        System.out.println("===== 대기열 진입 완료 =====");
    }

}