package dev.memory.coupon.user.repository;

import dev.memory.coupon.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

}

