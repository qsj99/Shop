package com.example.springdemo.dto;

import com.example.springdemo.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {
    private String searchDateType;   //현재 시간과 상품 등록일을 비교해서 상품 데이터를 조회함

    private ItemSellStatus searchSellStatus;  //상품 판매 상태를 기준으로 상품 데이터 조회

    private String searchBy;  //상품 조회할 때 어떤 유형으로 조회할지 선택 itemNm, createBy(상품 등록자 아이디)

    private String searchQuery = "";  //검색어 저장할 변수, searchBy가 itemNm일 경우 상품명 기준으로 검색, createBy일 경우 상품 등록자 아이디 기준으로 검색

}
