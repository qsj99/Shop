package com.example.springdemo.repository;

import com.example.springdemo.constant.ItemSellStatus;
import com.example.springdemo.dto.ItemSearchDto;
import com.example.springdemo.dto.MainItemDto;
import com.example.springdemo.dto.QMainItemDto;
import com.example.springdemo.entity.Item;
import com.example.springdemo.entity.QItem;
import com.example.springdemo.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    private JPAQueryFactory queryFactory;   //동적으로 쿼리를 생성하기 위해 JPAQueryFactory 클래스 사용

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);   //생성자로 EntityManager 객체 넣어줌
    }

    //Querydsl 에서는 BooleanExpression이라는 where 절에서 사용할 수 있는 값을 지원함 ,
    //BooleanExpression을 반환하는 메소드를 만들고 해당 조건들을 다른 쿼리를 생성할 때 사용할 수 있음 => 코드 중복 줄임
    //상품 판매 상태 조건이 null일 경우는 null 리턴
    //결과값이 null이면 where 절에서 해당 조건은 무시됨
    //상픔 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //searchDateType의 값에 따라 date Time의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        }else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        }else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }
    //searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 생성자의 아이디에 검색어를 포함하고 있는
    //상품을 조회하도록 조건값을 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createBy", searchBy)) {
            return QItem.item.createBy.like("%" + searchQuery + "%");
        }
        return  null;
    }

    //queryFactory를 이용해서 쿼리 생성
    //selectFrom : 상품 데이터를 조회하기 위해서 Qitem의 item을 지정
    //where 조건절 : "," 단위로 넣어줄 경우 and 조건으로 인식함
    //offset : 데이터를 가지고 올 시작 인덱스 지정
    //limit : 한번에 가지고 올 최대 개수 지정
    //fetchResult() : 조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환,
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);   //조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환

    }

    private  BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(    //QMainItemDto의 생성자에 반환할 값들을 넣어줌
                                            //QueryProjection을 사용하면 DTO로 바로 조회가 가능함 엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있음
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)    //itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

}