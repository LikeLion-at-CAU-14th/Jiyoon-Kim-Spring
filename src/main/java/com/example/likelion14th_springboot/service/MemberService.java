package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.dto.request.JoinRequestDto;
import com.example.likelion14th_springboot.exception.DuplicateMemberNameException;
import com.example.likelion14th_springboot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {

        return memberRepository.findAll();
    }

    public Member getByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    public Page<Member> getMembersByPage(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return memberRepository.findAll(pageable);
    }

    public Page<Member> getAdultMembersSortedByName(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("name").ascending()
        );

        return memberRepository.findByAgeGreaterThanEqual(20, pageable);
    }

    public List<Member> getMembersByNamePrefix(String name) {
        return memberRepository.findByNameStartsWith(name);
    }

    // 비밀번호 인코더 DI(생성자 주입)
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinRequestDto joinRequestDto) {
        // 해당 name이 이미 존재하는 경우
        if (memberRepository.existsByName(joinRequestDto.getName())) {
            throw new DuplicateMemberNameException();
        }

        // 유저 객체 생성
        Member member = joinRequestDto.toEntity(bCryptPasswordEncoder);

        // 유저 정보 저장
        memberRepository.save(member);
    }
}
