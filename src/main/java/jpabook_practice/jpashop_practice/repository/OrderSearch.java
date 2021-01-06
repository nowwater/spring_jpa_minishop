package jpabook_practice.jpashop_practice.repository;

import jpabook_practice.jpashop_practice.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName; // ȸ�� �̸�
    private OrderStatus orderStatus; // �ֹ� ����(ORDER, CANCEL)

}
