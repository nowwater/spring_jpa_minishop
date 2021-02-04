package jpabook_practice.jpashop_practice.api;

import jpabook_practice.jpashop_practice.domain.Address;
import jpabook_practice.jpashop_practice.domain.Order;
import jpabook_practice.jpashop_practice.domain.OrderStatus;
import jpabook_practice.jpashop_practice.repository.OrderRepository;
import jpabook_practice.jpashop_practice.repository.OrderSearch;
import jpabook_practice.jpashop_practice.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook_practice.jpashop_practice.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
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

    @GetMapping("api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){ // List 로 반환하면 안되고, result로 감싸야함
        // ORDER 2개
        // N + 1 문제 : 1 + N (2번) : 첫 번째 쿼리의 결과로 N 번만큼 추가로 쿼리가 실행되는 것.
        // 1 : orders 를 불러오기 위한 쿼리.
        // N : 회워 N + 배송 N
        // 즉, 총 주문이 (N) 2개 이므로, 1 + 회원조회 2 + 배송 조회 2 = 5 개의 쿼리 발생!
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        // 결과 2개
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("api/v3/simple-orders")
    // fetch join 으로 쿼리 최적화, N+1 문제 해결 - 쿼리 1개로 모든 결과 조회
    // 장점 : 조회한 엔티티로 데이터 조작 가능
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect((Collectors.toList()));
        return result;
    }

    @GetMapping("api/v4/simple-orders")
    // 필요한 내용들을 조회하는 DTO를 가져오도록 쿼리를 직접 짰기 때문에 SQL 이 간단해진다.
    // 단점 : 재사용이 불가능, 해당 DTO를 사용할 때만 사용가능.., DTO로 가져왔기 때문에 데이터 조작X
    //      : JPQL을 직접 짜야해서 코드가 복잡해짐.
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();

    }
    // 여기서 한거는 ~ToOne 관계 조회를 최적화 하기 위함
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { // 중요하지 않은 DTO 에서 중요한 엔티티를 의존하는 것은 문제가 되지 않음
            orderId = order.getId();
            name = order.getMember().getName(); // Member 쿼리 발생 // LAZY 초기화 : 영속성 컨텍스트를 찾아보고 없으면 DB에서 가져옴.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Delivery 쿼리 발생
        }

        /*
        ORDER 조회 -> SQL 1번 -> 결과 주문 수가 2개
         */
    }
}
