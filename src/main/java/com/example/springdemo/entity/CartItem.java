package com.example.springdemo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    //하나의 장바구니에는 여러개의 상품을 담을 수 있음
    @ManyToOne(fetch = FetchType.LAZY)  //지연로딩 설정
    @JoinColumn(name="cart_id")
    private Cart cart;

    //하나의 상품은 여러 장바구니의 장바구니 상품으로 담길 수 있음
    @ManyToOne(fetch = FetchType.LAZY) //지연로딩 설정
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;  //같은 상품을 장바구니에 몇개 담을지

    //장바구니에 담을 상품 엔티티 생성 메서드, 장바구니에 담을 수량을 증가시켜 주는 메서드
    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }
    //장바구니에 기존에 담겨 있는 상품인데, 해당 상품을 추가로 장바구니에 담을 때 기존 수량에 현재 담을 수량을 더해줄 때 사용
    public void addCount(int count){
        this.count += count;
    }

    //현재 장바구니에 담겨있는 수량 변경
    public void updateCount(int count){
        this.count = count;
    }
}
