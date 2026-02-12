package dev.memory.coupon.coupon.v3.controller;

import dev.memory.coupon.coupon.v3.repository.CouponRepository;
import dev.memory.coupon.coupon.v3.service.CouponService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponControllerTest {

    @Autowired
    @Qualifier("couponServiceV3")
    private CouponService couponServiceV3;

    @Autowired
    @Qualifier("couponRepositoryV3")
    private CouponRepository couponRepositoryV3;

    @Test
    @DisplayName("Redission 분산락 적용 후 동시에 1000명이 요청하면 100개만 발급되어야 한다.")
    void concurrentCouponIssueTest() throws InterruptedException {

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i< threadCount; i++) {

            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    couponServiceV3.issueCoupon(userId);
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }
            });

        }

        latch.await();

        long count = couponRepositoryV3.countByCode("COUPON_V3");
        System.out.println("발급된 쿠폰 수: " + count);

        Assertions.assertEquals(100, count);

    }

}