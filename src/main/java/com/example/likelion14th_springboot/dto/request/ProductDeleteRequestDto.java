package com.example.likelion14th_springboot.dto.request;

import lombok.Getter;

@Getter
public class ProductDeleteRequestDto {
    private Long memberId; // 본인 상품만 삭제할 수 있도록 검증하기 위함
}