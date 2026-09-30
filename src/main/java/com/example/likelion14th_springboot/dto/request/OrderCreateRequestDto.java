package com.example.likelion14th_springboot.dto.request;

import com.example.likelion14th_springboot.domain.ShippingAddress;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequestDto {

    private Long buyerId;
    private List<OrderItemRequestDto> items;
    private ShippingAddressRequestDto shippingAddress;

    @Getter
    public static class OrderItemRequestDto {
        private Long productId;
        private Integer quantity;
    }

    @Getter
    public static class ShippingAddressRequestDto {
        private String recipient;
        private String phoneNumber;
        private String roadAddress;
        private String detailAddress;
        private String postalCode;

        public ShippingAddress toEmbeddable() {
            return ShippingAddress.builder()
                    .recipient(this.recipient)
                    .phoneNumber(this.phoneNumber)
                    .roadAddress(this.roadAddress)
                    .detailAddress(this.detailAddress)
                    .postalCode(this.postalCode)
                    .build();
        }
    }
}