package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderCreateRequestDto dto) {

        OrderResponseDto response = orderService.createOrder(dto);

        return ResponseEntity
                .created(URI.create("/orders/" + response.getOrderId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(
            @RequestParam("buyerId") Long buyerId) {

        return ResponseEntity.ok(
                orderService.getOrdersByBuyer(buyerId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable("id") Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrderById(orderId)
        );
    }

    // 자신의 주문만 조회 가능 - orderId랑 buyerId 모두 넘김
//    @GetMapping("/{id}")
//    public ResponseEntity<OrderResponseDto> getOrderById(
//            @PathVariable("id") Long orderId,
//            @RequestParam("buyerId") Long buyerId) {
//
//        return ResponseEntity.ok(
//                orderService.getOrderById(orderId, buyerId)
//        );
//    }
}