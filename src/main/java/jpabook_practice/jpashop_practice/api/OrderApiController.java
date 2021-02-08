package jpabook_practice.jpashop_practice.api;

import jpabook_practice.jpashop_practice.domain.Address;
import jpabook_practice.jpashop_practice.domain.Order;
import jpabook_practice.jpashop_practice.domain.OrderItem;
import jpabook_practice.jpashop_practice.domain.OrderStatus;
import jpabook_practice.jpashop_practice.repository.OrderRepository;
import jpabook_practice.jpashop_practice.repository.OrderSearch;
import jpabook_practice.jpashop_practice.repository.order.query.OrderFlatDto;
import jpabook_practice.jpashop_practice.repository.order.query.OrderItemQueryDto;
import jpabook_practice.jpashop_practice.repository.order.query.OrderQueryDto;
import jpabook_practice.jpashop_practice.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    /* 권장 순서

     1. 엔티티 조회 방식으로 우선 접근
          V3 : 페치조인으로 쿼리 수 최적화
          이후 컬렉션 최적화
          - 페이징 필요 : hibernate.default_batch_fetch_size, @BatchSize
          - 페이징 필요 X : 페치 조인 사용

     2. 엔티티 조회 방식으로 해결 안되면 (트래픽이 굉장히 많은 경우 - 보통은 DTO 를 캐시를 써서 최적화.. 엔티티는 영속성 컨텍스트와 캐시간에 문제 때문에 직접 캐싱하면 안됨!!)
     (레디스나 로컬 메모리 캐시를 이용해서 캐싱)
          V4 : DTO 조회 방식 사용

     3. DTO 조회 방식으로 해결 안되면
          NativeSQL or JDBC Template 사용

     */
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){ // 이런 식으로 엔티티 직접 노출 X~~
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // 강제 초기화
            order.getDelivery().getAddress(); // 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            /*for (OrderItem orderItem : orderItems) {// OrderItem 초기화
                orderItem.getItem().getName(); // Order 초기화
            }
            */
            orderItems.stream().forEach(o -> o.getItem().getName());
            // 양방향 관계는 꼭 Jsonignore를 붙여줘야함.
        }
        return all;
    }

    @GetMapping("/api/v2/orders") // 값 타입은 그냥 노출시켜도 되지만, 엔티티는 노출 X
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        // "1:다 조인" 이므로 DB row수가 증가한다. 따라서 order 엔티티 조회수도 증가하게 된다
        // 해결법 : distinct 키워드 사용! -> 같은 엔티티 조회시 어플리케이션에서 중복을 걸러줌.
        // 단점 : 페이징이 불가능하다. 페이징 시 메모리에 로딩해서 페이징해버림 -> 메모리 터져버림..
        
        // 1:다 조인에서는 컬렉션 페치 조인을 1개만 사용 가능. 안그럼 1:N:M... 복잡해져서 데이터가 부정합하게 조회됨
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        // 컬렉션 페치에서 페이징하는 방법.
        // 1. ToOne 관계의 필드들은 다 페치 조인으로 가져옴 (row 수에 영향 x)
        // 2. 컬렉션은 LAZY 로딩
        // 3. applicaiton.yml에 hibernate.default.batch_fetch_size: ~~. 한번에 조회할 개수 지정. => N + 1 문제 해결
        // 3번을 안쓰면 지연로딩을 해서 쿼리수가 많이 나가는데, 사용하면 한번에 미리 ~개씩 가져오기 때문에 대부분 한번의 쿼리로 처리 가능
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){ // 에러에 F2 누르면 이동함
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){ // v4 보다 쿼리수가 줄어들음(2번 보냄). 메모리에 맵으로 한번에 미리 다 저장시켜놔서 조회 시 O(1)
        return orderQueryRepository.findAllByDto_Optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        // OrderFlatDto 말고 OrderQueryDto 로 통일해야한다면
        // flats 에서 필요한 필드들을 꺼내서 만들면 됨!

        // 장점 : 쿼리 1번
        // 단점 : 애플리케이션에서 추가 작업이 크다. 페이징 불가능
        // 쿼리는 한 번이지만 조인으로 인해 DB에서 어플리케이션에 전달하는 데이터에 중복 데이터가 추가되므로 상황에따라 V5 보다 느릴 수 있다.
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Data // Property 관련 에러는 대부분 자바의 Getter Setter 관련 문제
    // @Data 말고, @Getter 만 쓰는게 나을 수도 있음
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // private List<OrderItem> orderItems; // 엔티티여서 API로 결과가 안나옴.
        // OrderItemDto 로 바꿔야함!!!
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); // 엔티티가 API를 통해 외부로 노출되어버림.. 안좋다!!!
            // 완전히 엔티티에 대한 의존을 다 끊어내야함.
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new )// 람다 형태로 최적화 가능
                    .collect(toList()); // static import로 가능
        }
    }

    @Getter
    static class OrderItemDto { // 노출시키고 싶은 내용
        // 가상 요구사항 : 상품 이름, 주문 가격, 주문 수량만 필요

        private String itemName; // 상품 명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
        // API JSON 응답 결과에 루트를 [] 가 아닌 {} 로 바꿔주는게 좋다 -> 오브젝트 형태로 래핑해서
    }
}
