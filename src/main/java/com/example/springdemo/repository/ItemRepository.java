package com.example.springdemo.repository;

import com.example.springdemo.dto.ItemFormDto;
import com.example.springdemo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom{


    List<Item> findByItemNm (String itemNm);

        //or조건 처리하기
        List<Item> findByItemNmOrItemDetail (String itemNm, String itemDetail);

        //LessThan 조건 처리
        List<Item> findByPriceLessThan (Integer price);

        //OrderBy로 정렬하기
        List<Item> findByPriceLessThanOrderByPriceDesc (Integer price);

        //@Query 를 사용한 검색 처리
        @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
        List<Item> findByItemDetail (@Param("itemDetail") String itemDetail);

        //nativeQuery를 사용한 예제 (데이터베이스에서 사용한 쿼리를 그대로 사용해야할 때 사용함)
        @Query(value = "select * from item i where i.item_detail like " +
                "%:itemDetail% order by i.price desc", nativeQuery = true)
        List<Item> findByItemDetailByNative (@Param("itemDetail") String itemDetail);






}
