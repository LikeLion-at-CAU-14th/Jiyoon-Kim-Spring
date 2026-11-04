package com.example.likelion14th_springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuth2Controller {

    @GetMapping("/oauth2/success")
    @ResponseBody
    public String oauth2Success() {
        return "Login Success";
    }
}