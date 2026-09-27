package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.dto.request.ProductCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.ProductResponseDto;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("판매자는 상품을 성공적으로 등록할 수 있다.")
    void createProduct_Success() {
        // given
        Member seller = Member.builder().name("판매자").role(Role.SELLER).build();
        ReflectionTestUtils.setField(seller, "id", 1L); // 안정성을 위해 ID 주입

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "멋사 티셔츠");
        ReflectionTestUtils.setField(requestDto, "price", 15000);
        ReflectionTestUtils.setField(requestDto, "stock", 50);
        ReflectionTestUtils.setField(requestDto, "description", "14기 굿즈");
        ReflectionTestUtils.setField(requestDto, "memberId", 1L);

        Product product = requestDto.toEntity(seller);

        given(memberRepository.findById(1L)).willReturn(Optional.of(seller));
        given(productRepository.save(any(Product.class))).willReturn(product);

        // when
        ProductResponseDto responseDto = productService.createProduct(requestDto);

        // then
        assertThat(responseDto.getName()).isEqualTo("멋사 티셔츠");
        assertThat(responseDto.getPrice()).isEqualTo(15000);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("판매자가 아닌 구매자가 상품을 등록하려 하면 예외가 발생한다.")
    void createProduct_Fail_NotSeller() {
        // given
        Member buyer = Member.builder().name("구매자").role(Role.BUYER).build();
        ReflectionTestUtils.setField(buyer, "id", 2L);

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "멋사 티셔츠");
        ReflectionTestUtils.setField(requestDto, "memberId", 2L);

        given(memberRepository.findById(2L)).willReturn(Optional.of(buyer));

        // when & then
        assertThatThrownBy(() -> productService.createProduct(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품은 판매자만 등록할 수 있습니다.");
    }

    @Test
    @DisplayName("전체 상품 목록을 조회할 수 있다.")
    void getAllProducts_Success() {
        // given
        Product product1 = Product.builder().name("상품1").price(1000).stock(10).description("설명1").build();
        Product product2 = Product.builder().name("상품2").price(2000).stock(20).description("설명2").build();

        given(productRepository.findAll()).willReturn(List.of(product1, product2));

        // when
        List<ProductResponseDto> result = productService.getAllProducts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("상품1");
        assertThat(result.get(1).getName()).isEqualTo("상품2");
    }

    @Test
    @DisplayName("특정 ID의 상품을 성공적으로 조회한다.")
    void getProductById_Success() {
        // given
        Product product = Product.builder().name("멋사 키링").price(5000).stock(100).description("이쁨").build();
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        ProductResponseDto result = productService.getProductById(1L);

        // then
        assertThat(result.getName()).isEqualTo("멋사 키링");
        assertThat(result.getPrice()).isEqualTo(5000);
    }

    @Test
    @DisplayName("본인의 상품을 성공적으로 수정할 수 있다.")
    void updateProduct_Success() {
        // given
        Member seller = Member.builder().name("판매자").role(Role.SELLER).build();
        ReflectionTestUtils.setField(seller, "id", 1L); // NullPointerException 방지

        Product product = Product.builder().name("구상품").price(1000).stock(10).description("구설명").seller(seller).build();

        ProductUpdateRequestDto updateDto = new ProductUpdateRequestDto();
        ReflectionTestUtils.setField(updateDto, "name", "신상품");
        ReflectionTestUtils.setField(updateDto, "price", 2000);
        ReflectionTestUtils.setField(updateDto, "stock", 20);
        ReflectionTestUtils.setField(updateDto, "description", "신설명");
        ReflectionTestUtils.setField(updateDto, "memberId", seller.getId());

        given(memberRepository.findById(any())).willReturn(Optional.of(seller));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        ProductResponseDto result = productService.updateProduct(1L, updateDto);

        // then
        assertThat(result.getName()).isEqualTo("신상품");
        assertThat(result.getPrice()).isEqualTo(2000);
        assertThat(product.getStock()).isEqualTo(20);
    }

    @Test
    @DisplayName("본인이 등록한 상품을 성공적으로 삭제할 수 있다.")
    void deleteProduct_Success() {
        // given
        Member seller = Member.builder().name("판매자").role(Role.SELLER).build();
        ReflectionTestUtils.setField(seller, "id", 1L); // NullPointerException 방지

        Product product = Product.builder().name("삭제할상품").price(1000).stock(10).description("설명").seller(seller)
                .build();

        ProductDeleteRequestDto deleteDto = new ProductDeleteRequestDto();
        ReflectionTestUtils.setField(deleteDto, "memberId", seller.getId());

        given(memberRepository.findById(any())).willReturn(Optional.of(seller));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        productService.deleteProduct(1L, deleteDto);

        // then
        verify(productRepository).delete(product);
    }
}