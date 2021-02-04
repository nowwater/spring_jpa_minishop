package jpabook_practice.jpashop_practice.repository.order.simplequery;

import jpabook_practice.jpashop_practice.domain.Address;
import jpabook_practice.jpashop_practice.domain.Order;
import jpabook_practice.jpashop_practice.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) { // 중요하지 않은 DTO 에서 중요한 엔티티를 의존하는 것은 문제가 되지 않음
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}

