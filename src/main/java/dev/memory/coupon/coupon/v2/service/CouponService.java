package dev.memory.coupon.coupon.v2.service;

import dev.memory.coupon.coupon.v2.entity.Coupon;
import dev.memory.coupon.coupon.v2.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("couponServiceV2")
@RequiredArgsConstructor
public class CouponService {

    private static final int MAX_COUPON_COUNT = 100;
    private static final String COUPON_CODE = "COUPON_V2";

    @Qualifier("couponRepositoryV2")
    private final CouponRepository couponRepository;

    public synchronized void issueCoupon(Long userId) {

        // 중복 발급 체크
        if (couponRepository.existsByUserId(userId)) {
            throw new IllegalStateException("이미 쿠폰을 발급받았습니다.");
        }

        // 선착순 수량 체크
        long issuedCount = couponRepository.countByCode(COUPON_CODE);
        if (issuedCount >= MAX_COUPON_COUNT) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // 쿠폰 발급
        Coupon coupon = Coupon.create(userId, COUPON_CODE);
        couponRepository.save(coupon);

    }


}
