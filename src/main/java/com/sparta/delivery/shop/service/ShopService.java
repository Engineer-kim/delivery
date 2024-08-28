package com.sparta.delivery.shop.service;

import com.sparta.delivery.product.Product;
import com.sparta.delivery.shop.dto.ShopData;
import com.sparta.delivery.shop.dto.ShopRequest;
import com.sparta.delivery.shop.dto.ShopResponse;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.exception.StoreException;
import com.sparta.delivery.shop.repo.ShopRepository;
import com.sparta.delivery.shop.statusEnum.ShopDataStatus;
import com.sparta.delivery.shop.statusEnum.ShopPrivacyStatus;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRepository;
import com.sparta.delivery.user.UserRoleEnum;
import com.sparta.delivery.user.dto.UserInfoDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository storeRepository;
    private final UserRepository userRepository;

    /**가게 정보 추가*/
    @Transactional
    public ShopResponse addStore(ShopRequest shopRequest , Long userId, List<Product> products) {
        try {
            User user = getUserAndCheckAuthorization(userId);

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
    /**가게 정보 조회(다건)*/
    public List<ShopData> getAllShops(Long userId) {
        User user = getUserAndCheckAuthorization(userId);
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    /**가게 정보 상세 조회(단건)*/
    public ShopData getOneShop(Long userId, Long id) {
        User user = getUserAndCheckAuthorization(userId);
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다."));
        return convertToDto(store);
    }
    /**가게 정보 가게 정보 수정*/
    @Transactional
    public ShopData updateShopInfo(Long userId, Long id  , ShopRequest updateRequest) {
        User user = getUserAndCheckAuthorization(userId);
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        //유저와 상품, 가게 식별 번호 제외 수정 가능하도록 + 널 인지 체크
        Store updateResult = Store.builder()
                .shopId(store.getShopId())
                .shopName(updateRequest.getShopName() != null ? updateRequest.getShopName() : store.getShopName())
                .shopAddress(updateRequest.getShopAddress() != null ? updateRequest.getShopAddress() : store.getShopAddress())
                .shopType(updateRequest.getShopType() != null ? updateRequest.getShopType() : store.getShopType())
                .shopOpenTime(updateRequest.getShopOpenTime() != null ? updateRequest.getShopOpenTime() : store.getShopOpenTime())
                .shopCloseTime(updateRequest.getShopCloseTime() != null ? updateRequest.getShopCloseTime() : store.getShopCloseTime())
                .shopPhone(updateRequest.getShopPhone() != null ? updateRequest.getShopPhone() : store.getShopPhone())
                .userId(userId)
                .products(store.getProducts())
                .build();

        System.out.println(" updateResult.getShopOpenTime()::::::::::::::;" +  updateResult.getShopOpenTime());
        System.out.println(" updateResult. store.getShopOpenTime()()::2wqeqweqeq2e::::::::::::;" +   store.getShopOpenTime());
        System.out.println(" updateResult.getClosedTime()::::::::::::::::::;" +  updateResult.getShopCloseTime());

        Store updatedStore = storeRepository.save(updateResult);

        // DTO로 변환하여 반환
        return convertToDto(updatedStore);
    }
    /**가게 정보 삭제*/
    @Transactional
    public void deleteShop(Long id, Long userId) {
        User user = getUserAndCheckAuthorization(userId);
        Store findResult = storeRepository.findByIdAndDeleteStatus(id, ShopDataStatus.U)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));
        findResult.setDeleteStatus(ShopDataStatus.D);
        storeRepository.save(findResult);
    }

    /**가게 비공개 처리*/
    @Transactional
    public void makePrivateShop(Long id, Long userId) {
        User user = getUserAndCheckAuthorization(userId);
        Store findResult = storeRepository.findByIdAndPrivacyStatus(id, ShopPrivacyStatus.P)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));
        findResult.setPrivacyStatus(ShopPrivacyStatus.R);
        storeRepository.save(findResult);
    }

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
        return role == UserRoleEnum.OWNER || role == UserRoleEnum.MASTER;
    }

    private User getUserAndCheckAuthorization(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"사용자를 찾을 수 없습니다."));

        if (!isUserAuthorized(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        return user;
    }

}
