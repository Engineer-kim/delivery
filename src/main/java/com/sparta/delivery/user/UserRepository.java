package com.sparta.delivery.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<com.sparta.delivery.user.User, Long> {
    Optional<User> findByUsername(String username);
    // 삭제되지 않은 유저만 조회
    List<User> findAllByIsDeletedFalse();
}