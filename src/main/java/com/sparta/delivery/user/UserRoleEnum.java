package com.sparta.delivery.user;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

    //사용자 권한 (CUSTOMER, OWNER, MANAGER, MASTER)
    CUSTOMER(Authority.CUSTOMER),
    OWNER(Authority.OWNER),
    MANAGER(Authority.MANAGER),
    MASTER(Authority.MASTER);  // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    // 스프링 시큐리티로 구현할 것이라 "ROLE_" prefix 붙임
    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }
}
