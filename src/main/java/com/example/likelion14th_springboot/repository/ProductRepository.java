package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// <매핑할 Entity 클래스, ID의 PK 데이터 타입>
public interface ProductRepository extends JpaRepository<Product, Long> {
}