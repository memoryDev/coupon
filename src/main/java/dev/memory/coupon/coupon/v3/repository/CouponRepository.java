package dev.memory.coupon.coupon.v3.repository;

import dev.memory.coupon.coupon.v3.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("couponRepositoryV3")
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    boolean existsByUserId(Long userId);
    long countByCode(String code);

}
