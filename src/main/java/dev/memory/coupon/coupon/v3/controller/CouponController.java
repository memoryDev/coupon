package dev.memory.coupon.coupon.v3.controller;

import dev.memory.coupon.coupon.v3.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("couponControllerV3")
@RequiredArgsConstructor
@RequestMapping("/api/v3/coupons")
public class CouponController {

    @Qualifier("couponServiceV3")
    private final CouponService couponService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> issueCoupon(@PathVariable("userId") Long userId) {
        couponService.issueCoupon(userId);
        return ResponseEntity.ok("쿠폰 발급 성공!");
    }
}
