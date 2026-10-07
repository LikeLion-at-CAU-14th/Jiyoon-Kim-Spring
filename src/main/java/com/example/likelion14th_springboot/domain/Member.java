package com.example.likelion14th_springboot.domain;

import com.example.likelion14th_springboot.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String password;
    private String address;
    private String email;
    private String phoneNumber;
    private Integer age;

    @Builder
    public Member(String name, String address, String email, String phoneNumber, Integer age, Role role, Boolean isAdmin, Integer deposit, String password) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isAdmin = isAdmin;
        this.deposit = deposit;
        this.age = age;
        this.password = password;
    }

    @Enumerated(EnumType.STRING)
    private Role role; // 판매자면 SELLER, 구매자면 BUYER

    private Boolean isAdmin; // 관리자 계정 여부

    private Integer deposit; // 현재 계좌 잔액

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public void chargeDeposit(int money) {
        this.deposit += money;
    }

    public void useDeposit(int money) {
        this.deposit -= money;
    }

    //추가
    public boolean isSeller() {
        return Role.SELLER.equals(this.role);
    }
}