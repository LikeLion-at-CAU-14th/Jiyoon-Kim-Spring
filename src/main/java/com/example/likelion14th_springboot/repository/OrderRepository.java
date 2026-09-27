package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllByBuyer_IdOrderByIdDesc(Long buyerId);

    // Optional<Orders> findByIdAndBuyer_Id(Long orderId, Long buyerId);
}