package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    // Optional<Orders> findByIdAndBuyer_Id(Long orderId, Long buyerId);

    // 삭제되지 않은 주문만 조회
    List<Orders> findAllByBuyer_IdAndDeletedFalseOrderByIdDesc(Long buyerId);

    Optional<Orders> findByIdAndDeletedFalse(Long orderId);
}