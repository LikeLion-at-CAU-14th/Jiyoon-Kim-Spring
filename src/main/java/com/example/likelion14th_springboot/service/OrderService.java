package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.OrderRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto dto) {
        // 1. 요청값 확인
        validateRequest(dto);

        // 2. 구매자 조회 및 역할 확인
        Member buyer = memberRepository.findById(dto.getBuyerId())
                .orElseThrow(() ->
                        new IllegalArgumentException("구매자를 찾을 수 없습니다."));

        if (buyer.getRole() != Role.BUYER) {
            throw new IllegalArgumentException("구매자만 주문할 수 있습니다.");
        }

        // 3. 주문 객체 생성
        Orders order = Orders.builder()
                .buyer(buyer)
                .shippingAddress(dto.getShippingAddress().toEmbeddable())
                .build();

        // 4. 상품 조회 후 주문에 추가
        for (OrderCreateRequestDto.OrderItemRequestDto item : dto.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "상품을 찾을 수 없습니다. ID: " + item.getProductId()
                            ));

            order.addProduct(product, item.getQuantity());
        }

        // 5. 주문 저장 및 응답 변환
        Orders savedOrder = orderRepository.save(order);
        return OrderResponseDto.fromEntity(savedOrder);
    }

    private void validateRequest(OrderCreateRequestDto dto) {
        if (dto.getBuyerId() == null) {
            throw new IllegalArgumentException("구매자 ID는 필수입니다.");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문할 상품이 필요합니다.");
        }

        for (OrderCreateRequestDto.OrderItemRequestDto item : dto.getItems()) {
            if (item == null || item.getProductId() == null) {
                throw new IllegalArgumentException("상품 ID는 필수입니다.");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
            }
        }

        OrderCreateRequestDto.ShippingAddressRequestDto address =
                dto.getShippingAddress();

        if (address == null) {
            throw new IllegalArgumentException("배송정보는 필수입니다.");
        }

        if (isBlank(address.getRecipient())
                || isBlank(address.getPhoneNumber())
                || isBlank(address.getRoadAddress())
                || isBlank(address.getDetailAddress())
                || isBlank(address.getPostalCode())) {
            throw new IllegalArgumentException("배송정보 5개 항목을 모두 입력해주세요.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}