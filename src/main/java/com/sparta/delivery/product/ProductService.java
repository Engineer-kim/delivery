package com.sparta.delivery.product;


import com.sparta.delivery.product.dto.PageDto;
import com.sparta.delivery.product.dto.ProductAddRequestDto;
import com.sparta.delivery.product.dto.ProductAddResponseDto;
import com.sparta.delivery.product.dto.ProductSingleResponse;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.repo.ShopRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public ProductAddResponseDto addProduct(ProductAddRequestDto productRequestDto) {
        Product product;
        Store store;

        if(productRequestDto.shopId() == null) {
            product = Product.builder()
                .productName(productRequestDto.productName())
                .description(productRequestDto.description())
                .price(productRequestDto.price())
                .build();

            productRepository.save(product);

            return ProductAddResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .build();
        }
        else {
            store = shopRepository.findById(UUID.fromString(productRequestDto.shopId())).orElseThrow(() -> new RuntimeException("가게 정보를 찾을 수 없습니다."));

            product = Product.builder()
                .store(store)
                .productName(productRequestDto.productName())
                .description(productRequestDto.description())
                .price(productRequestDto.price())
                .build();

            productRepository.save(product);

            return ProductAddResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .storeId(store.getShopId())
                .build();
        }



    }

    public PageDto getAllSearch(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by("productName").descending());
        Page<Product> products = productRepository.findAll(sortedPageable);

        var data = products.getContent().stream()
            .map(ProductSingleResponse::new)
            .toList();

        return new PageDto(data,
            products.getTotalElements(),
            products.getTotalPages(),
            pageable.getPageNumber(),
            data.size()
        );
    }

    public ResponseEntity<ProductSingleResponse> getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("해당 ID의 상품을 찾을 수 없습니다."));
        ProductSingleResponse response = new ProductSingleResponse(product);
        return ResponseEntity.ok().body(response);
    }

    public ProductSingleResponse updateProduct(UUID productId, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setProductName(productDetails.getProductName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setPublic(productDetails.isPublic());
            productRepository.save(product);
            return new ProductSingleResponse(product);
        } else {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }
    }

    public void deleteProduct(UUID productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setDeleted(true);
            productRepository.save(product);
        } else {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }
    }

    public PageDto getAllSearch(Pageable pageable, String search) {
        Page<Product> productPage;

        if (search != null && !search.isEmpty()) {
            productPage = productRepository.findByProductNameContaining(search, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductSingleResponse> data = productPage.getContent().stream()
            .map(ProductSingleResponse::new)
            .collect(Collectors.toList());

        return new PageDto(
            data,
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }
}

