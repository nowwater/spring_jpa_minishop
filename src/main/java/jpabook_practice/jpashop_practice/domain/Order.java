package jpabook_practice.jpashop_practice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id") // FK 이름
    private Member member;

    // OrderItem.order 에 의해 매핑
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // cascade 를 통해 persist 를 전파
    // 모든 엔티티는 저장하려면 각각 persist 해줘야함.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    // => access를 많이 하는 쪽(연관관계의 주인)에 FK를 두는게 유리함!

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 : [ORDER, CANCEL]

    //==생성 메서드==// 장점 : 변경사항이 있으면 여기만 고치면 됌!!!
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){ // ...은 여려개 넘길 수 있음
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==연관관계 메서드==//
    // 양방향에 데이터가 추가되어야하는 관계들. 컨트롤하는쪽에 추가해주는 것이 좋다.
    // Order와 Member
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // Order와 OrderItem
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // Order와 Delivery
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==비즈니스 로직==//
    /*
    주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP) // 이미 배송 완료
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /*
    전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
