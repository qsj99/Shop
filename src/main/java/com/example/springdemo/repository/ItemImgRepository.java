package com.example.springdemo.repository;

import com.example.springdemo.entity.Item;
import com.example.springdemo.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    //상품의 대표 이미지를 찾는 쿼리 메소드, 구매 이력에 대표이미지 보여주기
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);

}
