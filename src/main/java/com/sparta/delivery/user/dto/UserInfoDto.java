package com.sparta.delivery.user.dto;

import com.sparta.delivery.user.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    String username;
    UserRoleEnum role;
}