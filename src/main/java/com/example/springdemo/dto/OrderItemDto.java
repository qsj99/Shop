package com.example.springdemo.dto;

import com.example.springdemo.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {

    public OrderItemDto(OrderItem orderItem, String imgUrl){   //OrderItemDto 클래스의 생성자로 orderItem 객체와 이미지 경로를 파라미터로 받아서
                                                                //멤버변수 값 세팅
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;

    }

    private String itemNm;
    private int count;
    private int orderPrice;
    private String imgUrl;

}
