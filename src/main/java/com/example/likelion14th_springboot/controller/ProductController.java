package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.ProductCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.ProductResponseDto;
import com.example.likelion14th_springboot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products") // URL 공통 경로 매핑
public class ProductController {

    private final ProductService productService;

    // [POST] http://localhost:8080/products -> 상품 등록
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateRequestDto dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }


    // 전체 상품 목록 조회: GET http://localhost:8080/products
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 특정 상품 상세 조회: GET http://localhost:8080/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // 특정 상품 수정: PUT http://localhost:8080/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id,
                                                            @RequestBody ProductUpdateRequestDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    // 특정 상품 삭제: DELETE http://localhost:8080/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id,
                                                @RequestBody ProductDeleteRequestDto dto) {
        productService.deleteProduct(id, dto);
        return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
    }
}