package dev.memory.coupon.coupon.v4.service;

import dev.memory.coupon.coupon.v4.entity.Coupon;
import dev.memory.coupon.coupon.v4.repository.CouponRepository;
import dev.memory.coupon.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("couponServiceV4")
@RequiredArgsConstructor
public class CouponService {

    private static final int MAX_COUPON_COUNT = 1;
    private static final String COUPON_CODE = "COUPON_V4";
    private static final String LOCK_KEY = "coupon:lock:v4";

    private final CouponRepository couponRepositoryV4;
    private final RedissonClient redissonClient;
    private final QueueService queueService;

    public boolean tryImmediateIssue(Long userId) {
        RLock lock = redissonClient.getLock(LOCK_KEY);

        try {
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
            if (!available) {
                return false;
            }

            // 중복 발급 체크
            if (couponRepositoryV4.existsByUserId(userId)) {
                throw new IllegalArgumentException("이미 쿠폰을 발급받았습니다.");
            }

            // 쿠폰 남았는지 확인
            long issuedCount = couponRepositoryV4.countByCode(COUPON_CODE);
            if (issuedCount >= MAX_COUPON_COUNT) {
                return false; // 쿠폰 소진 → 대기열로
            }

            // 쿠폰 발급
            Coupon coupon = Coupon.create(userId, COUPON_CODE);
            couponRepositoryV4.save(coupon);
            return true; // 발급 성공

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void issueCouponFromQueue(Long userId) {
        // 1. 대기열 체크 - 발급 가능한지 확인
        Long rank = queueService.getRank(userId);
        if (rank == null) {
            throw new IllegalArgumentException("대기열에 등록되지 않았습니다.");
        }
        if (rank > 100) {
            throw new IllegalArgumentException("아직 순번이 되지 않았습니다. 현재 순번: " + rank);
        }

        // 2. Redis 분산락으로 동시성 제어
        RLock lock = redissonClient.getLock(LOCK_KEY);

        try {
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
            if (!available) {
                return;
            }

            // 중복 발급 체크
            if (couponRepositoryV4.existsByUserId(userId)) {
                throw new IllegalArgumentException("이미 쿠폰을 발급받았습니다.");
            }

            // 선착순 수량 체크
            long issuedCount = couponRepositoryV4.countByCode(COUPON_CODE);
            if (issuedCount >= MAX_COUPON_COUNT) {
                throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
            }

            // 쿠폰 발급
            Coupon coupon = Coupon.create(userId, COUPON_CODE);
            couponRepositoryV4.save(coupon);

            // 대기열 제거
            queueService.removeFromQueue(userId);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
