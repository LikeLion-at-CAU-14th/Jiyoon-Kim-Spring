package com.example.likelion14th_springboot.domain;

import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DeliverStatus deliverStatus = DeliverStatus.PREPARATION;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @Embedded
    private ShippingAddress shippingAddress;

    @Builder.Default
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<ProductOrders> productOrders = new ArrayList<>();

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Coupon coupon;

    public void addProduct(Product product, Integer quantity) {
        ProductOrders orderItem = ProductOrders.builder()
                .orders(this)
                .product(product)
                .quantity(quantity)
                .build();

        this.productOrders.add(orderItem);
    }

    public void updateShippingAddress(ShippingAddress shippingAddress) {
        if (this.deliverStatus != DeliverStatus.PREPARATION) {
            throw new IllegalArgumentException(
                    "배송 준비 중인 주문만 배송정보를 수정할 수 있습니다."
            );
        }

        this.shippingAddress = shippingAddress;
    }
}