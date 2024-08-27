package com.sparta.delivery.product;


import com.sparta.delivery.product.dto.PageDto;
import com.sparta.delivery.product.dto.ProductAddRequestDto;
import com.sparta.delivery.product.dto.ProductAddResponseDto;
import com.sparta.delivery.product.dto.ProductSingleResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {


    private final ProductService productService;

    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER') or hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<ProductAddResponseDto> addProduct(
        @RequestBody ProductAddRequestDto productRequestDto) {
        ProductAddResponseDto newProduct = productService.addProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @GetMapping
    public ResponseEntity<PageDto> getAllProducts(Pageable pageable) {
        PageDto products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSingleResponse> getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER') or hasRole('MASTER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id,
        @RequestBody Product productDetails) {
        try {
            ProductSingleResponse updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 상품을 찾을 수 없습니다.");
        }
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER') or hasRole('MASTER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 상품을 찾을 수 없습니다.");
        }
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER') or hasRole('MASTER')")
    @GetMapping("/search")
    public ResponseEntity<PageDto> getAllProducts(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort", defaultValue = "createdDate,desc") String sort,
        @RequestParam(value = "search", required = false) String search) {

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Sort sortBy = Sort.by(direction, sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sortBy);
        PageDto pageDto = productService.getAllProducts(pageable, search);
        return ResponseEntity.ok(pageDto);
    }

}

