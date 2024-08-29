package com.sparta.delivery.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderSearchRequestDto {
    private String shopName; // 상점 이름
    private int pageNumber; // 페이지 번호 (기본값: 0)
    private int pageSize; // 페이지 크기 (기본값: 10)
    private String sortField; // 정렬 기준 (기본값: createdAt)
    private String sortDirection; // 정렬 방향 (기본값: DESC)
}