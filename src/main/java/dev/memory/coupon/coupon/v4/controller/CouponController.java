package dev.memory.coupon.coupon.v4.controller;

import dev.memory.coupon.coupon.v4.service.CouponService;
import dev.memory.coupon.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v4/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponServiceV4;
    private final QueueService queueService;

    @PostMapping("/issue/{userId}")
    public ResponseEntity<?> issueCoupon(@PathVariable("userId") Long userId) {

        // 1. 즉시발급 시도
        boolean issued = couponServiceV4.tryImmediateIssue(userId);
        if (issued) {
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "쿠폰이 발급되었습니다."));
        }

        // 2. 쿠폰 소진 -> 대기열 진입
        Long rank = queueService.joinQueue(userId);
        return ResponseEntity.ok(Map.of(
                "status", "QUEUE",
                "message", "쿠폰이 소진되어 대기열에 등록되었습니다.",
                "rank", rank
        ));
    }

    // 대기열에서 쿠폰 발급 (순번 도달 시)
    @PostMapping("/issue-from-queue/{userId}")
    public ResponseEntity<String> issueCouponFromQueue(@PathVariable Long userId) {
        couponServiceV4.issueCouponFromQueue(userId);
        return ResponseEntity.ok("쿠폰이 발급되었습니다.");
    }
}
