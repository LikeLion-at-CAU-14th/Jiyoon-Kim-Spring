package com.example.likelion14th_springboot.dto.request;

import com.example.likelion14th_springboot.domain.ShippingAddress;
import lombok.Getter;

@Getter
public class OrderUpdateRequestDto {

    private String recipient;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String postalCode;

    public ShippingAddress toEmbeddable() {
        return ShippingAddress.builder()
                .recipient(recipient)
                .phoneNumber(phoneNumber)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .postalCode(postalCode)
                .build();
    }
}