package com.example.springdemo.repository;

import com.example.springdemo.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    //현재 로그인한 회원의 Cart 엔티티를 찾기 위한 쿼리 메서드
    Cart findByMemberId(Long memberId);
}
