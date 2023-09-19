package com.example.springdemo.entity;

import com.example.springdemo.constant.ItemSellStatus;
import com.example.springdemo.dto.ItemFormDto;
import com.example.springdemo.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //상품 코드

    @Column(nullable = false, length=50)
    private String itemNm;   //상품명

    @Column(name = "price", nullable = false)
    private int price;   //상품가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;  //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  //상품 판매 상태

    //상품 업데이트하는 로직 추가(비즈니스 로직)
    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //재고 감소시키는 로직
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber; //상품의 재고 수량에서 주문 후 남은 재고 수량 구함
        if(restStock<0) {
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량 :" + this.stockNumber + ")");
        }
        this.stockNumber = restStock;   //주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당함

        }

        //상품 재고 증가시키는 메소드
        public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
        }
}


