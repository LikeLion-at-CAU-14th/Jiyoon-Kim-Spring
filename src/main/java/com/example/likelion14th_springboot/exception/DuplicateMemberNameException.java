package com.example.likelion14th_springboot.exception;

public class DuplicateMemberNameException extends RuntimeException {

    public DuplicateMemberNameException() {
        super("이미 사용 중인 이름입니다.");
    }
}