package com.sparta.delivery.shop.service;

import com.sparta.delivery.product.Product;
import com.sparta.delivery.shop.dto.ShopData;
import com.sparta.delivery.shop.dto.ShopRequest;
import com.sparta.delivery.shop.dto.ShopResponse;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.exception.StoreException;
import com.sparta.delivery.shop.repo.ShopRepository;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRoleEnum;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository storeRepository;

    /**가게 정보 추가*/
    public ShopResponse addStore(ShopRequest shopRequest , UserInfoDto userInfoDto, List<Product> products) {
        try {
            // 사용자 역할 체크
            if (!isUserAuthorized(userInfoDto.getRole())) {
                throw new StoreException("가게 정보를 추가할 권한이 없습니다.", null);
            }
            Store store = convertToEntity(shopRequest, userInfoDto, products);
            Store savedStore = storeRepository.save(store);
            ShopData shopData = convertToDto(savedStore);
            return ShopResponse.builder()
                    .statusCode(201)
                    .status("success")
                    .message("가게 정보가 추가되었습니다.")
                    .data(shopData)
                    .build();
        } catch (Exception e) {
            throw new StoreException("가게 정보 추가 중 오류 발생", e);
        }
    }
    /**가게 정보 조회*/
    /**가게 정보 상세 조회*/
    /**가게 정보 가게 정보 수정*/
    /**가게 정보 삭제*/
    /**가게 비공개 처리*/




    //  Entity ->  DTO
    private ShopData convertToDto(Store savedStore) {
        try{
            return ShopData.builder()
                    .shopId(savedStore.getShopId())
                    .shopName(savedStore.getShopName())
                    .shopAddress(savedStore.getShopAddress())
                    .shopType(savedStore.getShopType())
                    .shopOpenTime(savedStore.getShopOpenTime())
                    .shopOpenTime(savedStore.getShopOpenTime())
                    .shopClosedTime(savedStore.getShopCloseTime())
                    .shopPhone(savedStore.getShopPhone())
                    .build();
        }catch (Exception e) {
            throw new RuntimeException("가게 쪽 Entity 에서 DTO 변환 중 오류: " + e.getMessage(), e);
        }

    }
    // Dto -> Entity
    private Store convertToEntity(ShopRequest shopRequest, UserInfoDto userInfoDto, List<Product> products) {
        try {
            User user = new User();
            user.setUsername(userInfoDto.getUsername());
            user.setRole(userInfoDto.getRole());
            return Store.builder()
                    .shopName(shopRequest.getShopName())
                    .shopAddress(shopRequest.getShopAddress())
                    .shopType(shopRequest.getShopType())
                    .shopOpenTime(shopRequest.getShopOpenTime())
                    .shopCloseTime(shopRequest.getShopCloseTime())
                    .shopPhone(shopRequest.getShopPhone())
                    .user(user)
                    .products(products)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("가게 쪽 DTO 에서 Entity 변환 중 오류: " + e.getMessage(), e);
        }
    }

    private boolean isUserAuthorized(UserRoleEnum role) {
        return role == UserRoleEnum.MANAGER || role == UserRoleEnum.MASTER;
    }

    public List<ShopData> getAllShops() {
        return null;
    }
}
