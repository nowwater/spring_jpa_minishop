package jpabook_practice.jpashop_practice.api;

import jpabook_practice.jpashop_practice.domain.Order;
import jpabook_practice.jpashop_practice.repository.OrderRepository;
import jpabook_practice.jpashop_practice.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne 관계 (ManyToOne, OneToOne)
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() { // 엔티티 직접 노출. 굉장히 안좋은 방법.
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        // 지연 로딩되서 반환하는데, 프록시 객체는 Hibernate5Module 에 의해서 null 로 바껴서 나감 -> 굳이 이거 때문에 모듈을 끌어다가 와서 쓰는거는 좋지 않음..
        // 필요하지 않은 내용들도 싹다 조회해버려서 성능낭비가 어마어마함...
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }

        return all; // 양방향 연관관계 : Order에서 meber, member에서 orders ... 무한루프에 빠져버림
        // API 응답 결과에서 배열로 감싸는건 안좋음 -> Wrapper 클래스 만들어서 씌워서 Object 형태로 내보내기.

        // 어찌됐건 DTO로 변환해서 사용하는것이 최고다!!!!!!
    }
}
