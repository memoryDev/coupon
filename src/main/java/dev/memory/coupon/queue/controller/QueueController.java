package dev.memory.coupon.queue.controller;

import dev.memory.coupon.queue.dto.QueueStatusResponse;
import dev.memory.coupon.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v4/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

        @PostMapping("/join")
        public ResponseEntity<Long> joinQueue(Long userId) {
            Long rank = queueService.joinQueue(userId);
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@PathVariable("userId") Long userId) {

        QueueStatusResponse queueStatus = queueService.getQueueStatus(userId);
        return ResponseEntity.ok(queueStatus);

    }
}
