package dev.memory.coupon.coupon.v4.repository;

import dev.memory.coupon.coupon.v4.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("couponRepositoryV4")
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    boolean existsByUserId(Long userId);
    long countByCode(String code);

}
