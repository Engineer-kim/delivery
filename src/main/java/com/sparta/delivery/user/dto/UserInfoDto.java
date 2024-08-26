package com.sparta.delivery.user.dto;

import com.sparta.delivery.user.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    String username;
    UserRoleEnum role;
}