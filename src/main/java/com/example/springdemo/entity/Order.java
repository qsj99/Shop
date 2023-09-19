package com.example.springdemo.entity;

import com.example.springdemo.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //한명의 회원은 여러번 주문할 수 있으므로 주문 엔티티 기준에서 다대일 단방향
    @ManyToOne(fetch = FetchType.LAZY)  //지연로딩 설정
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //주문 상품 엔티티아 일대다 매핑 외래키가 order_item 테이블에 있으므로 연관관계의 주인은 OrderItem 엔티티
    //cascade 옵션 : 부모 엔ㅌ티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 옵션 : CascadeType.ALL
    //고아 객체 제거 사요아는 어노테이션 : orphanRemoval = true
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    //셍성한 주문 상품 객체를 이용하여 주문 객체를 만드는 메소드
    public  void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return  totalPrice;
    }

    //주문 취소 시 주문 수량을 상품의 재고에 더해주고 주문 상태를 취소 상태로 바꿔주는 메소드
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }

    }}
