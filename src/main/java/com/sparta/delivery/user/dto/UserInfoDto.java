package com.sparta.delivery.user.dto;

import com.sparta.delivery.address.dto.UserAddressResponseDto;
import com.sparta.delivery.user.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String username;
    private UserRoleEnum role;
    private List<UserAddressResponseDto> addressList;
}