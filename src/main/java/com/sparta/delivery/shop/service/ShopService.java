package com.sparta.delivery.shop.service;

import com.sparta.delivery.common.ApiResponse;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.shop.dto.ShopData;
import com.sparta.delivery.shop.dto.ShopRequest;
import com.sparta.delivery.shop.dto.ShopResponse;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.exception.StoreException;
import com.sparta.delivery.shop.repo.ShopRepository;
import com.sparta.delivery.common.statusEnum.DataStatus;
import com.sparta.delivery.common.statusEnum.PrivacyStatus;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRepository;
import com.sparta.delivery.user.UserRoleEnum;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository storeRepository;
    private final UserRepository userRepository;

    /**가게 정보 추가*/
    @Transactional
    public ShopResponse addStore(ShopRequest shopRequest, Long userId, List<Product> products) {
        try {
            User user = getUserAndCheckAuthorization(userId);
            Store store = convertToEntity(shopRequest, userId, products);

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

    /**가게 정보 조회(다건)*/
    public List<ShopData> getAllShops(Long userId) {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    /**가게 정보 상세 조회(단건)*/
    public ShopData getOneShop(Long userId, UUID id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다."));
        return convertToDto(store);
    }
    /**가게 정보 가게 정보 수정*/
    @Transactional
    public ShopData updateShopInfo(Long userId, UUID id  , ShopRequest updateRequest) {
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
                .deleteStatus(DataStatus.U)
                .privacyStatus(PrivacyStatus.P)
                .products(store.getProducts())
                .build();

        Store updatedStore = storeRepository.save(updateResult);

        // DTO로 변환하여 반환
        return convertToDto(updatedStore);
    }
    /**가게 정보 삭제*/
    @Transactional
    public ApiResponse deleteShop(UUID id, Long userId) {
        User user = getUserAndCheckAuthorization(userId);
        try {
            Store findResult = storeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));
            if (findResult.getDeleteStatus() == DataStatus.D) {
                throw new IllegalStateException("가게는 이미 삭제되었습니다.");
            }
            findResult.setDeleteStatus(DataStatus.D);
            storeRepository.save(findResult);
            return new ApiResponse(200, "success", "해당 가게 미사용(삭제)상태로 처리되었습니다", null);
        } catch (EntityNotFoundException e) {
            return new ApiResponse(404, "fail", "가게를 찾을 수 없습니다. 유효한 가게 ID를 입력했는지 확인해 주세요.", null);
        } catch (IllegalStateException e) {
            return new ApiResponse(400, "fail", "가게는 이미 삭제된 상태입니다", null);
        } catch (Exception e) {
            return new ApiResponse(500, "fail", "가게 삭제 처리 도중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

    /**가게 비공개 처리*/
    @Transactional
    public ApiResponse makePrivateShop(UUID id, Long userId) {
        User user = getUserAndCheckAuthorization(userId);
        try {
            Store findResult = storeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("해당 ID의 가게를 찾을 수 없습니다."));
            if (findResult.getDeleteStatus() == DataStatus.D) {
                throw new IllegalStateException("가게는 이미 비공개 처리 된 상태입니다.");
            }
            findResult.setPrivacyStatus(PrivacyStatus.R);
            storeRepository.save(findResult);
            return new ApiResponse(200, "success", "해당 가게 비공개상태로 처리되었습니다", null);
        } catch (EntityNotFoundException e) {
            return new ApiResponse(404, "fail", "가게를 찾을 수 없습니다. 유효한 ID를 입력했는지 확인해 주세요.", null);
        } catch (IllegalStateException e) {
            return new ApiResponse(400, "fail", "가게는 이미 비공개 처리 된 상태입니다", null);
        }catch (Exception e) {
            return new ApiResponse(500, "fail", "가게 비공개 처리 도중 오류가 발생했습니다: " + e.getMessage(), null);
        }
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
