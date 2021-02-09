package jpabook_practice.jpashop_practice.service;

import jpabook_practice.jpashop_practice.domain.*;
import jpabook_practice.jpashop_practice.domain.item.Item;
import jpabook_practice.jpashop_practice.repository.ItemRepository;
import jpabook_practice.jpashop_practice.repository.MemberRepository;
import jpabook_practice.jpashop_practice.repository.OrderRepository;
import jpabook_practice.jpashop_practice.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional // 데이터 변경
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        
        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);// 트랜잭션이 커밋되는 시점에 flush가 발생하면 insert 쿼리가 날아감.
        return order.getId();
    }

    // 취소
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }

    /*
    도메인 모델 패턴
    엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것
    서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할.
    트랜잭션 스크립트 패턴
    엔티티에 비즈니스 로직이 거의 없고 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것
    일반적으로 SQL 방식을 써서 쭉 써내려가면서 짜는 방법
     */
}
