package dev.memory.coupon.coupon.v3.service;

import dev.memory.coupon.coupon.v3.entity.Coupon;
import dev.memory.coupon.coupon.v3.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("couponServiceV3")
public class CouponService {

    private static final int MAX_COUPON_COUNT = 100;
    private static final String COUPON_CODE = "COUPON_V3";
    private static final String LOCK_KEY = "coupon:lock";


    private final CouponRepository couponRepositoryV3;
    private final RedissonClient redissonClient;

    public CouponService(
            @Qualifier("couponRepositoryV3") CouponRepository couponRepository,
            RedissonClient redissonClient
    ) {
        this.redissonClient = redissonClient;
        this.couponRepositoryV3 = couponRepository;
    }

    public void issueCoupon(Long userId) {

        RLock lock = redissonClient.getLock(LOCK_KEY);

        try {

            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if (!available) {
                //throw new IllegalStateException("락 획득 실패");
                return;
            }

            // 중복 발급 체크
            if (couponRepositoryV3.existsByUserId(userId)) {
                throw new IllegalStateException("이미 쿠폰을 발급받았습니다.");
            }

            // 선착순 수량 체크
            long issuedCount = couponRepositoryV3.countByCode(COUPON_CODE);
            if (issuedCount >= MAX_COUPON_COUNT) {
                throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
            }

            // 쿠폰 발급
            Coupon coupon = Coupon.create(userId, COUPON_CODE);
            couponRepositoryV3.save(coupon);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }




    }



}
