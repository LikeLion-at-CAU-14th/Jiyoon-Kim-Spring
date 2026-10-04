package com.example.likelion14th_springboot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private LocalDateTime timestamp;
}