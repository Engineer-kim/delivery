package com.sparta.delivery.shop.service;

import com.sparta.delivery.product.Product;
import com.sparta.delivery.shop.dto.ShopData;
import com.sparta.delivery.shop.dto.ShopRequest;
import com.sparta.delivery.shop.dto.ShopResponse;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.exception.StoreException;
import com.sparta.delivery.shop.repo.ShopRepository;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRepository;
import com.sparta.delivery.user.UserRoleEnum;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository storeRepository;
    private final UserRepository userRepository;

    /**가게 정보 추가*/
    @Transactional
    public ShopResponse addStore(ShopRequest shopRequest , Long userId, List<Product> products) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            if (!isUserAuthorized(user.getRole())) {
                throw new RuntimeException("권한이 없습니다.");
            }

            Store store = convertToEntity(shopRequest, userId, products);
            System.out.println("UserId:::::::::" + userId);
            Store savedStore = storeRepository.save(store);
            System.out.println("UserId:::::::::1" + userId);
            ShopData shopData = convertToDto(savedStore);
            System.out.println("UserId:::::::::2" + userId);
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
                    .userId(savedStore.getUserId())
                    .build();
        }catch (Exception e) {
            throw new RuntimeException("가게 쪽 Entity 에서 DTO 변환 중 오류: " + e.getMessage(), e);
        }

    }
    // Dto -> Entity
    private Store convertToEntity(ShopRequest shopRequest, Long userId, List<Product> products) {
        try {
            Store store = Store.builder()
                    .shopName(shopRequest.getShopName())
                    .shopAddress(shopRequest.getShopAddress())
                    .shopType(shopRequest.getShopType())
                    .shopOpenTime(shopRequest.getShopOpenTime())
                    .shopCloseTime(shopRequest.getShopCloseTime())
                    .shopPhone(shopRequest.getShopPhone())
                    .userId(userId)
                    .products(products)
                    .build();
            System.out.println("Built Store userId: " + store.getUserId()); // 생성된 스토어의 userId 출력
            return  store;
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
