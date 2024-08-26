package com.sparta.delivery.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressRequestDto {
    private String addressName;
    private String line1;
    private String line2;
}
