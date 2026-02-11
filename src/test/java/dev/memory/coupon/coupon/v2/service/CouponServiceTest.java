package dev.memory.coupon.coupon.v2.service;

import dev.memory.coupon.coupon.v2.repository.CouponRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService couponServiceV2;

    @Autowired
    private CouponRepository couponRepositoryV2;

    @Test
    @DisplayName("")
    void issueCouponTest() throws InterruptedException {

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i< threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    couponServiceV2.issueCoupon(userId);

                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }
            });

        }

        latch.await();

        long count = couponRepositoryV2.countByCode("COUPON_V2");
        System.out.println("발급된 쿠폰 수: " + count);

        Assertions.assertEquals(100, count);
    }

}