package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.OrderRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

        // 3. 같은 상품이 여러 번 들어오면 수량 합산
        Map<Long, Integer> quantities = new LinkedHashMap<>();

        for (OrderCreateRequestDto.OrderItemRequestDto item : dto.getItems()) {
            int previousQuantity =
                    quantities.getOrDefault(item.getProductId(), 0);

            long combinedQuantity =
                    (long) previousQuantity + item.getQuantity();

            if (combinedQuantity > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("주문 수량이 너무 큽니다.");
            }

            quantities.put(item.getProductId(), (int) combinedQuantity);
        }

        // 4. 상품별 재고 확인 및 총 주문 금액 계산
        Map<Long, Product> products = new HashMap<>();
        long totalPrice = 0L;

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "상품을 찾을 수 없습니다. ID: " + productId
                            ));

            if (product.getStock() == null
                    || product.getStock() < quantity) {
                throw new IllegalArgumentException(
                        "상품 재고가 부족합니다: " + product.getName()
                );
            }

            if (product.getPrice() == null || product.getPrice() < 0) {
                throw new IllegalArgumentException(
                        "상품 가격이 올바르지 않습니다: " + product.getName()
                );
            }

            totalPrice += (long) product.getPrice() * quantity;

            // 현재 잔액 필드와 차감 메서드가 Integer/int 타입이므로 제한
            if (totalPrice > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "주문 금액이 처리 가능한 범위를 초과했습니다."
                );
            }

            products.put(productId, product);
        }

        // 5. 구매자 잔액 확인
        if (buyer.getDeposit() == null
                || buyer.getDeposit() < totalPrice) {
            throw new IllegalArgumentException("계좌 잔액이 부족합니다.");
        }

        // 6. 주문 생성
        Orders order = Orders.builder()
                .buyer(buyer)
                .shippingAddress(dto.getShippingAddress().toEmbeddable())
                .build();

        // 7. 상품 재고 차감 및 주문 상품 추가
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = products.get(entry.getKey());
            int quantity = entry.getValue();

            product.reduceStock(quantity);
            order.addProduct(product, quantity);
        }

        // 8. 구매자 잔액 차감
        buyer.useDeposit((int) totalPrice);

        // 9. 주문 저장 및 응답 반환
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

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByBuyer(Long buyerId) {
        if (!memberRepository.existsById(buyerId)) {
            throw new IllegalArgumentException("구매자를 찾을 수 없습니다.");
        }

        return orderRepository.findAllByBuyer_IdAndDeletedFalseOrderByIdDesc(buyerId)
                .stream()
                .map(OrderResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Orders order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return OrderResponseDto.fromEntity(order);
    }


    // 자신의 주문만 조회 가능
//    @Transactional(readOnly = true)
//    public OrderResponseDto getOrderById(Long orderId, Long buyerId) {
//        Orders order = orderRepository.findByIdAndBuyer_Id(orderId, buyerId)
//                .orElseThrow(() ->
//                        new IllegalArgumentException(
//                                "주문이 존재하지 않거나 조회할 수 없는 주문입니다."
//                        ));
//
//        return OrderResponseDto.fromEntity(order);
//    }

    @Transactional
    public OrderResponseDto updateShippingAddress(
            Long orderId, OrderUpdateRequestDto dto) {

        // 1. 배송정보 입력값 확인
        if (isBlank(dto.getRecipient())
                || isBlank(dto.getPhoneNumber())
                || isBlank(dto.getRoadAddress())
                || isBlank(dto.getDetailAddress())
                || isBlank(dto.getPostalCode())) {
            throw new IllegalArgumentException(
                    "배송정보 5개 항목을 모두 입력해주세요."
            );
        }

        // 2. 주문 조회
        Orders order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 3. 엔티티 메서드에서 배송 상태 확인 후 수정
        order.updateShippingAddress(dto.toEmbeddable());

        // 4. 변경된 정보 반환
        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Orders order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "주문이 존재하지 않거나 이미 삭제되었습니다."
                        ));

        order.softDelete();
    }
}