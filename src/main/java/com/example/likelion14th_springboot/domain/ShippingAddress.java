package com.example.likelion14th_springboot.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShippingAddress {

    private String recipient;       // 수령인
    private String phoneNumber;     // 전화번호
    private String roadAddress;     // 도로명주소
    private String detailAddress;   // 상세주소
    private String postalCode;      // 우편번호
}