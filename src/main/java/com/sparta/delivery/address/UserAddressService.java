package com.sparta.delivery.address;

import com.sparta.delivery.address.dto.UserAddressRequestDto;
import com.sparta.delivery.address.dto.UserAddressResponseDto;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserService;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // 엔티티 -> dto 변환
    public UserAddressResponseDto convertToDto(UserAddress userAddress) {
        return new UserAddressResponseDto(userAddress.getId(), userAddress.getAddressName(), userAddress.getLine1(), userAddress.getLine2());
    }

    public List<UserAddressResponseDto> getMyAllAddresses(Long userId) {
        List<UserAddress> userAddressList = userAddressRepository.findByUserId(userId);
        return userAddressList.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
