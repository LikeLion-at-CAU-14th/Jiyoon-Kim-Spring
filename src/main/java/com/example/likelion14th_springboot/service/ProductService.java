package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.dto.request.ProductCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.ProductResponseDto;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 만들어 의존성을 주입함
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequestDto dto) {
        // 1. 판매자 회원 정보 조회 (없으면 예외 발생)
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 판매자입니다."));

        // 2. 판매자(SELLER) 권한 확인
        if (!member.isSeller()) {
            throw new IllegalArgumentException("상품은 판매자만 등록할 수 있습니다.");
        }

        // 3. DTO를 실제 Entity로 변환하고 DB에 저장
        Product product = dto.toEntity(member);
        Product saved = productRepository.save(product);

        // 4. 저장된 Entity를 ResponseDto로 변환하여 반환
        return new ProductResponseDto(saved.getId(), saved.getName(), saved.getPrice(), saved.getStock(), saved.getDescription());
    }

    // 1. 모든 상품 목록 가져오기
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::fromEntity) // 엔티티 스트림을 DTO 스트림으로 변환
                .toList();
    }

    // 2. 특정 ID의 상품 1개 가져오기
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return ProductResponseDto.fromEntity(product);
    }

    // 상품 정보 업데이트
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductUpdateRequestDto dto) {
        // 1. 판매자 조회
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 판매자가 존재하지 않습니다."));

        // 2. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // 3. 본인이 등록한 상품이 맞는지 권한 검증!
        if (!product.getSeller().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 상품만 수정할 수 있습니다.");
        }

        // 4. 상품 정보 수정 -> @Transactional 내부에 있으므로 변경 감지(Dirty Checking)가 동작하여 자동 UPDATE!
        product.update(dto.getName(), dto.getPrice(), dto.getStock(), dto.getDescription());

        return ProductResponseDto.fromEntity(product);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId, ProductDeleteRequestDto dto) {
        // 1. 판매자 조회
        Member seller = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        // 2. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 3. 권한 확인 (본인 상품인지)
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("본인의 상품만 삭제할 수 있습니다.");
        }

        // 4. DB에서 상품 완전 삭제
        productRepository.delete(product);
    }
}