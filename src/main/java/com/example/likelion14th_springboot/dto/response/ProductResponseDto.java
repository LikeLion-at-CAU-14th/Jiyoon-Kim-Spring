package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 추가!
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer stock;
    private String description;

    // [추가] Entity를 DTO로 깔끔하게 변환해 주는 정적 팩토리 메서드
    public static ProductResponseDto fromEntity(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .build();
    }
}