package dev.memory.coupon.coupon.v1.repository;

import dev.memory.coupon.coupon.v1.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("couponRepositoryV1")
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    boolean existsByUserId(Long userId);
    long countByCode(String code);

}
