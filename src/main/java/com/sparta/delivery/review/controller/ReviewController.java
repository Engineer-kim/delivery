package com.sparta.delivery.review.controller;

import com.sparta.delivery.common.ApiResponse;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.shop.dto.ShopRequest;
import com.sparta.delivery.shop.dto.ShopResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    @PostMapping("/review")
    public ResponseEntity<ApiResponse> addStore(@RequestBody ShopRequest shopRequest,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        Product product = productService.getProductById(shopRequest.getProductId()).getBody().getProduct();
        List<Product> productList = Collections.singletonList(product);
        ShopResponse shopResponse = storeService.addStore(shopRequest ,userId, productList);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(
                shopResponse.getStatusCode(),
                shopResponse.getStatus(),
                shopResponse.getMessage(),
                shopResponse
        ));
    }
}
