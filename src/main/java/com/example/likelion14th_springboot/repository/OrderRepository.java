package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}