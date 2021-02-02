package jpabook_practice.jpashop_practice.repository;

import jpabook_practice.jpashop_practice.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상테(ORDER, CANCEL)

}
