package com.sparta.delivery.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponseDto {
    private UUID id;
    private String addressName;
    private String line1;
    private String line2;
}
