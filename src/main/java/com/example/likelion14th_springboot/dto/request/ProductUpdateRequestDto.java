package com.example.likelion14th_springboot.dto.request;

import lombok.Getter;

@Getter
public class ProductUpdateRequestDto {
    private String name;
    private Integer price;
    private Integer stock;
    private String description;
    private Long memberId; // 수정을 요청한 사람이 실제 판매 주인인지 확인용
}