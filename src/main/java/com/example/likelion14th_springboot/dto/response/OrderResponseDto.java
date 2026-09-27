package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private Long buyerId;
    private DeliverStatus deliverStatus;
    private ShippingAddressResponseDto shippingAddress;
    private List<OrderItemResponseDto> items;

    public static OrderResponseDto fromEntity(Orders order) {
        return new OrderResponseDto(
                order.getId(),
                order.getBuyer().getId(),
                order.getDeliverStatus(),
                ShippingAddressResponseDto.from(order.getShippingAddress()),
                order.getProductOrders().stream()
                        .map(OrderItemResponseDto::fromEntity)
                        .toList()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class ShippingAddressResponseDto {
        private String recipient;
        private String phoneNumber;
        private String roadAddress;
        private String detailAddress;
        private String postalCode;

        public static ShippingAddressResponseDto from(ShippingAddress address) {
            return new ShippingAddressResponseDto(
                    address.getRecipient(),
                    address.getPhoneNumber(),
                    address.getRoadAddress(),
                    address.getDetailAddress(),
                    address.getPostalCode()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class OrderItemResponseDto {
        private Long productId;
        private String productName;
        private Integer quantity;

        public static OrderItemResponseDto fromEntity(ProductOrders item) {
            return new OrderItemResponseDto(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity()
            );
        }
    }
}