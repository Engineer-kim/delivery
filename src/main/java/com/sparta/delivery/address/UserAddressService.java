package com.sparta.delivery.address;

import com.sparta.delivery.address.dto.UserAddressRequestDto;
import com.sparta.delivery.address.dto.UserAddressResponseDto;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRoleEnum;
import com.sparta.delivery.user.UserService;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserService userService;


    public UserAddressResponseDto createAddress(UserAddressRequestDto requestDto, Long userId) {
        User user = userService.findUserById(userId);

        UserAddress userAddress = UserAddress.builder()
                .addressName(requestDto.getAddressName())
                .line1(requestDto.getLine1())
                .line2(requestDto.getLine2())
                .user(user)
                .build();
        UserAddress savedAddress = userAddressRepository.save(userAddress);
        return convertToDto(savedAddress);
    }

    // 내 주소 모두 조회
    public List<UserAddressResponseDto> getMyAllAddresses(Long userId) {
        List<UserAddress> userAddressList = userAddressRepository.findByUserIdAndIsDeletedFalse(userId);
        return userAddressList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 주소 단건 조회
    public UserAddressResponseDto getAddressById(UUID addressId) {
        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        return convertToDto(userAddress);
    }

    // 주소 삭제
    public void deleteAddress(UUID addressId, Long userId, UserRoleEnum userRole) {
        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (userAddress.isDeleted()) {
            throw new IllegalArgumentException("Address has already been deleted");
        }

        // 주소의 소유자 확인
        if (!userAddress.getUser().getId().equals(userId) && !userRole.equals(UserRoleEnum.MASTER)) {
            throw new SecurityException("Unauthorized access to address");
        }

        // 삭제 필드 설정
        userAddress.setDeletedAt(LocalDateTime.now());
        userAddress.setDeletedBy(userService.getCurrentUser());
        userAddress.setDeleted(true);
        userAddressRepository.save(userAddress);
    }


    // 주소 수정
    @Transactional
    public UserAddressResponseDto updateAddress(UUID addressId, UserAddressRequestDto requestDto, Long currentUserId, UserRoleEnum currentUserRole) {
        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // 권한 검증 : 자신의 주소이거나 MASTER 만 수정 가능
        if (!userAddress.getUser().getId().equals(currentUserId) && currentUserRole != UserRoleEnum.MASTER) {
            throw new SecurityException("Unauthorized access");
        }

        // 선택적 업데이트
        if (requestDto.getAddressName() != null) {
            userAddress.setAddressName(requestDto.getAddressName());
        }
        if (requestDto.getLine1() != null) {
            userAddress.setLine1(requestDto.getLine1());
        }
        if (requestDto.getLine2() != null) {
            userAddress.setLine2(requestDto.getLine2());
        }

        UserAddress updatedAddress = userAddressRepository.save(userAddress);

        return convertToDto(updatedAddress);
    }


    // MASTER : 모든 주소 조회
    public List<UserAddressResponseDto> getAllAddresses() {
        List<UserAddress> userAddresses = userAddressRepository.findAll();
        return userAddresses.stream()
                .filter(address -> !address.isDeleted()) // 삭제된 주소는 제외
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 엔티티 -> dto 변환
    public UserAddressResponseDto convertToDto(UserAddress userAddress) {
        return new UserAddressResponseDto(userAddress.getId(), userAddress.getAddressName(), userAddress.getLine1(), userAddress.getLine2());
    }

}
