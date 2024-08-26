package com.sparta.delivery.address;

import com.sparta.delivery.address.dto.UserAddressRequestDto;
import com.sparta.delivery.address.dto.UserAddressResponseDto;
import com.sparta.delivery.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
