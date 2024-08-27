package com.sparta.delivery.shop.controller;

import com.sparta.delivery.common.ApiResponse;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.product.ProductService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService storeService;
    private final UserService userService;
    private final ProductService productService;

    /**거게 정보 추가*/
    @PostMapping
    public ResponseEntity<ApiResponse> addStore(@RequestBody ShopRequest shopRequest) {
        UserInfoDto user = userService.getUserInfo(shopRequest.getUserId());
        List<Product> productList = (List<Product>) productService.getProductById(shopRequest.getProductId());
        ShopResponse shopResponse = storeService.addStore(shopRequest ,user, productList);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(
                shopResponse.getStatusCode(),
                shopResponse.getStatus(),
                shopResponse.getMessage(),
                shopResponse
        ));
    }
    /**가게 정보 전체 조회*/
    @GetMapping
    public ResponseEntity<ApiResponse> getAllShops() {
        try {
            List<ShopData> shopDataList = storeService.getAllShops();
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
                    "가게 조회 도중 오류가 발생했습니다",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
