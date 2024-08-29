package com.sparta.delivery.shop.controller;

import com.sparta.delivery.common.ApiResponse;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.product.ProductService;
import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.shop.dto.ShopData;
import com.sparta.delivery.shop.dto.ShopRequest;
import com.sparta.delivery.shop.dto.ShopResponse;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.exception.StoreException;
import com.sparta.delivery.shop.service.ShopService;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRoleEnum;
import com.sparta.delivery.user.UserService;
import com.sparta.delivery.user.dto.UserInfoDto;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShopController {

    private final ShopService storeService;
    private final UserService userService;
    private final ProductService productService;

    /**거게 정보 추가*/
    @PostMapping("/shops")
    public ResponseEntity<ApiResponse> addStore(@RequestBody ShopRequest shopRequest,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();

        // productList를 초기화
        List<Product> productList = new CopyOnWriteArrayList<>();//쓰기작업이 많지않으면서 멀티쓰레드 환경에서 적합함

        // productId가 존재하는 경우에만 product를 추가합니다.
        if (shopRequest.getProductId() != null) {
            Product product = productService.getProductById(shopRequest.getProductId())
                .getBody()
                .getProduct();
            productList.add(product);
        }

        ShopResponse shopResponse = storeService.addStore(shopRequest, userId, productList);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse(
                shopResponse.getStatusCode(),
                shopResponse.getStatus(),
                shopResponse.getMessage(),
                shopResponse
            ));
    }


    /**가게 정보 전체 조회*/
    @GetMapping("/shops")
    public ResponseEntity<ApiResponse> getAllShops(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getUser().getId();
            List<ShopData> shopDataList = storeService.getAllShops(userId);
            ApiResponse response = new ApiResponse(
                    200,
                    "success",
                    null,
                    shopDataList.isEmpty() ? Collections.emptyList() : shopDataList
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(
                    500,
                    "fail",
                    "가게 전체 조회 도중 오류가 발생했습니다",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**가게 정보 상세 조회*/
    @GetMapping("/shops/{id}")
    public ResponseEntity<ApiResponse> getOneShop(@AuthenticationPrincipal UserDetailsImpl userDetails ,@PathVariable UUID id) {
        try {
            Long userId = userDetails.getUser().getId();
            ShopData findOne = storeService.getOneShop(userId , id);
            ApiResponse response = new ApiResponse(
                    200,
                    "success",
                    null,
                    findOne
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(
                    500,
                    "fail",
                    "가게 단건 조회 도중 오류가 발생했습니다",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**가게 정보 가게 정보 수정*/
    @PutMapping("/shops/{id}")
    public ResponseEntity<ApiResponse> updateShopInfo(@AuthenticationPrincipal UserDetailsImpl userDetails ,@PathVariable UUID id,
                                                      @RequestBody ShopRequest updateRequest) {
        try {
            Long userId = userDetails.getUser().getId();
            ShopData updatedShopInfo = storeService.updateShopInfo(userId , id, updateRequest);
            ApiResponse response = new ApiResponse(
                    200,
                    "success",
                    null,
                    updatedShopInfo
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(
                    500,
                    "fail",
                    "가게 수정 도중 오류가 발생했습니다"  + e.getMessage(),
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**가게 정보 삭제*/
    @DeleteMapping("/shops/{id}")
    public ResponseEntity<ApiResponse> deleteShop(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
            Long userId = userDetails.getUser().getId();
            ApiResponse response =  storeService.deleteShop(id ,userId);
            return ResponseEntity.ok(response);
    }
    /**가게 비공게 처리*/
    @DeleteMapping("/shops/{id}/privacy")
    public ResponseEntity<ApiResponse> makePrivateShop(@PathVariable UUID id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        ApiResponse response = storeService.makePrivateShop(id, userId);
        return ResponseEntity.ok(response);
    }
}
