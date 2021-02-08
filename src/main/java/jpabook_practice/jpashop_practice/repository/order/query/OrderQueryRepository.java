package jpabook_practice.jpashop_practice.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders(); // query 1번 -> 2개 (N개) 즉, N+1 문제발생
        result.forEach(o ->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // query 2개 -> N번
            o.setOrderItems(orderItems);
        });
        return result;
    }
    // 위에는 쿼리를 루프 돌면서 여러번 날림.
    // 밑에는 쿼리를 한 번 날리고 메모리에서 Map으로 다 가져와서 매칭해서 값을 세팅해줌.
    public List<OrderQueryDto> findAllByDto_Optimization() {
        List<OrderQueryDto> result = findOrders(); // 쿼리 1번

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId()))); // 한 방에 메모리에 올리는 메서드
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery( // 쿼리 1번
                "select new jpabook_practice.jpashop_practice.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds) // data select 양이 줄어든다는 이점.
                .getResultList();
        //쿼리 총 2번 밖에 안날아감.

        // Key : orderId, Value : Dto
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook_practice.jpashop_practice.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery( // JPA 에서 DTO 직접 조회
                // 컬렉션은 생성자에서 못채움.
                "select new jpabook_practice.jpashop_practice.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() { // 장점 : 단 한번의 쿼리만 날아감.
        // 단점 : 어쩔 수 없이 1:N 에서 1쪽이 중복이 많이 됨. join의 특성.. => 페이징 불가
        return em.createQuery(
                "select new jpabook_practice.jpashop_practice.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
