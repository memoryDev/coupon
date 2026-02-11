package dev.memory.coupon.coupon.v2.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "CouponV2")
@Table(name = "coupons_v2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }

    public static Coupon create(Long userId, String code) {
        Coupon coupon = new Coupon();
        coupon.userId = userId;
        coupon.code = code;
        return coupon;
    }

}
