package dev.memory.coupon.coupon.service;

import dev.memory.coupon.coupon.v1.repository.CouponRepository;
import dev.memory.coupon.coupon.v1.service.CouponService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("동시에 1000명이 쿠폰을 요청하면 100개만 발급되어야 한다.")
    void concurrentCouponIssueTest() throws InterruptedException{

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i< threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(userId);
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long count = couponRepository.countByCode("COUPON_V1");
        System.out.println("발급된 쿠폰 수: " + count);

        Assertions.assertEquals(100, count);

    }



}