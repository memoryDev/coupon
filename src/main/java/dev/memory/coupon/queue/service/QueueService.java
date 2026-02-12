package dev.memory.coupon.queue.service;

import dev.memory.coupon.queue.dto.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private static final String QUEUE_KEY = "coupon:queue";
    private static final Long PROCESS_SIZE = 100L;

    private final RedisTemplate<String, String> redisTemplate;
    
    // 대기열 진입
    public Long joinQueue(Long userId) {
        long score = System.currentTimeMillis(); // 현재 시간을 점수
        redisTemplate.opsForZSet().add(QUEUE_KEY, String.valueOf(userId), score);

        return this.getRank(userId);
    }

    // 현재 대기 순번 조회
    public Long getRank(Long userId) {
        return redisTemplate.opsForZSet().rank(QUEUE_KEY, String.valueOf(userId));
    }

    // 대기열 상태 조회
    public QueueStatusResponse getQueueStatus(Long userId) {
        Long rank = getRank(userId);

        if (rank == null) {
            throw new IllegalStateException("대기열에 등록되지 않았습니다.");
        }

        // 100명 이내면 발급 가능
        boolean canIssue = rank <= PROCESS_SIZE;

        return QueueStatusResponse.of(userId, rank, canIssue);
    }

    // 대기열에서 제거
    public void removeFromQueue(Long userId) {
        redisTemplate.opsForZSet().remove(QUEUE_KEY, String.valueOf(userId));
    }



}
