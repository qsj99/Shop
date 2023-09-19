package com.example.springdemo.service;

import com.example.springdemo.dto.CartDetailDto;
import com.example.springdemo.dto.CartItemDto;
import com.example.springdemo.dto.CartOrderDto;
import com.example.springdemo.dto.OrderDto;
import com.example.springdemo.entity.Cart;
import com.example.springdemo.entity.CartItem;
import com.example.springdemo.entity.Item;
import com.example.springdemo.entity.Member;
import com.example.springdemo.repository.CartItemRepository;
import com.example.springdemo.repository.CartRepository;
import com.example.springdemo.repository.ItemRepository;
import com.example.springdemo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId())  //장바구니에 담을 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);  //현재 로그인한 회원 엔티티 조회

        Cart cart = cartRepository.findByMemberId(member.getId());  //현재 로그인한 회원의 장바구니 엔티티 조회
        if(cart == null){    //상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());  //현재 상품이 장바구니에 이미 들어가 있는지 조회
        if (savedCartItem != null ){
            savedCartItem.addCount(cartItemDto.getCount());  //장바구니에 이미 있는 상품일 경우 기존 수량에 현재 장바구니에 담을 수량만큼 더해줌
            return savedCartItem.getId();
        }else{
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());   //장바구니 엔티티, 상품 엔티티, 수량을 이용해서 CartItem 엔티티 생성
            cartItemRepository.save(cartItem);   //장바구니에 들어갈 상품 저장
            return cartItem.getId();

        }
    }

    //로그인한 회원의 정보를 이용하여 장바구니에 들어있는 상품 조회
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());  //현재 로그인한 회원의 장바구니 엔티티 조회
        if(cart == null){   //장바구니에 상품 한번도 안 담았을 경우 장바구니 엔티티가 없으므로 빈 리스트 반환
            return cartDetailDtoList;
        }
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());  //장바구니에 담겨있는 상품 정보를 조회

        return cartDetailDtoList;

    }

    //현재 로그인한 회원과 해당 장바구니 상품을 저장한 회원이 같은지 검사
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }
    //장바구니 상품 수량 업데이트
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }
    //장바구니 상품 삭제
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    //장바구니 상품 주문하기
    //주문 로직으로 전다랗ㄴ orderDto 리스트 생성 및 주문 로직 호출, 주문한 상품은 장바구니에서 제거
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);

        for (CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }
            return  orderId;
        }
    }


