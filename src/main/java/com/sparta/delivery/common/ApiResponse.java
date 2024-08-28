package com.sparta.delivery.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
/**응답 객체 상태코드와 함께 리턴하는 공통 템츨릿*/
public class ApiResponse {
    private int statusCode;
    private String status;
    private String message;
    private Object data;

}
