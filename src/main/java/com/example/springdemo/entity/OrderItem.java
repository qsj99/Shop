package com.example.springdemo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)   //하나의 상푸은 여러 주문 상품에 들어갈 수 있으므로 주문 상품 기준으로 다대일 단방향 매핑
    @JoinColumn(name = "item_id")  //JoinColumn은 매핑할 외래키 지정 name 속성에는 매핑할 외래키의 이름을 설정
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id")   //한번의 주문에 여러개의 상품을 주문할 수 있으므로 주문 상품 엔티티와 주문엔티티를 다대일 단방향 매핑
    private Order order;

    private int orderPrice;

    private int count;

    //주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메소드
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);   //주문할 상품과 주문 수량 세팅
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());   //상품가격을 주문 가격으로 세팅

        item.removeStock(count);   //주문 수량만큼 상품의 재고 수량 감소
        return orderItem;
    }
    public int getTotalPrice(){    //주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산
        return orderPrice*count;
    }

    //주문 취소 시 주문 수량만큼 상품의 재고 더해줌
    public void cancel(){
        this.getItem().addStock(count);
    }


}
