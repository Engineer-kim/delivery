package com.sparta.delivery.address;

import com.sparta.delivery.address.dto.UserAddressRequestDto;
import com.sparta.delivery.address.dto.UserAddressResponseDto;
import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.user.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserAddressController {

    private final UserAddressService userAddressService;


    // 내 주소 생성
    @PostMapping("/addresses")
    public ResponseEntity<UserAddressResponseDto> createUserAddress(
            @RequestBody UserAddressRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        UserAddressResponseDto responseDto = userAddressService.createAddress(requestDto, userId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 내 주소 전체 조회
    @GetMapping("/addresses")
    public ResponseEntity<List<UserAddressResponseDto>> getMyAllAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        List<UserAddressResponseDto> responseDtoList = userAddressService.getMyAllAddresses(userId);
        return ResponseEntity.ok(responseDtoList);
    }

    // 주소 단건 조회
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<UserAddressResponseDto> getAddressById(@PathVariable UUID addressId) {
        UserAddressResponseDto responseDto = userAddressService.getAddressById(addressId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 주소 수정
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<UserAddressResponseDto> updateAddress(
            @PathVariable UUID addressId,
            @RequestBody UserAddressRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long currentUserId = userDetails.getUser().getId();
        UserRoleEnum currentUserRole = userDetails.getUser().getRole();

        try {
            UserAddressResponseDto updatedAddress = userAddressService.updateAddress(addressId, requestDto, currentUserId, currentUserRole);
            return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 주소 삭제
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable UUID addressId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        UserRoleEnum userRole = userDetails.getUser().getRole(); // 유저의 권한 정보를 가져옵니다
        userAddressService.deleteAddress(addressId, userId, userRole);
        return ResponseEntity.noContent().build();
    }


    // MASTER
    // 관리자가 모든 주소를 조회
    @Secured(UserRoleEnum.Authority.MASTER)
    @GetMapping("/addresses/all")
    public ResponseEntity<List<UserAddressResponseDto>> getAllAddresses() {
        List<UserAddressResponseDto> responseDtoList = userAddressService.getAllAddresses();
        return ResponseEntity.ok(responseDtoList);
    }
}
