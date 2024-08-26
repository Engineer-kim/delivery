package com.sparta.delivery.user.dto;

import com.sparta.delivery.user.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private UserRoleEnum role;


}