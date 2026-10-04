package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.JoinRequestDto;
import com.example.likelion14th_springboot.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final MemberService memberService;

    @PostMapping("/join")
    public void join(@RequestBody JoinRequestDto joinRequestDto) {
        memberService.join(joinRequestDto);
    }

}