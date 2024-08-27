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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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
    public ResponseEntity<ApiResponse> getOneShop(@AuthenticationPrincipal UserDetailsImpl userDetails ,@PathVariable Long id) {
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
    public ResponseEntity<ApiResponse> updateShopInfo(@AuthenticationPrincipal UserDetailsImpl userDetails ,@PathVariable Long id,
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
                    "가게 단건 조회 도중 오류가 발생했습니다",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**가게 정보 삭제*/
    @DeleteMapping("/shops/{id}")
    public ResponseEntity<ApiResponse> deleteShop(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getUser().getId();
            storeService.deleteShop(id ,userId);
            ApiResponse response = new ApiResponse(
                    200,
                    "success",
                    "해당 가게 미사용(삭제)상태로 처리되었습니다",
                    null
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(
                    500,
                    "fail",
                    "가게 삭제 처리 도중 오류가 발생했습니다",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    /**가게 정보 삭제*/
    @DeleteMapping("/shops/{id}/privacy")
    public ResponseEntity<ApiResponse> makePrivateShop(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getUser().getId();
            storeService.makePrivateShop(id ,userId );
            ApiResponse response = new ApiResponse(
                    200,
                    "success",
                    "해당 가게 비공개상태로 처리되었습니다",
                    null
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(
                    500,
                    "fail",
                    "가게 비공개 처리 도중 오류가 발생했습니다",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
