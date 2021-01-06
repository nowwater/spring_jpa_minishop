package jpabook_practice.jpashop_practice.domain;

import jpabook_practice.jpashop_practice.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
// Order과 Item 간에 ManyToMany 관게를 OneToMany 로 쪼개기 위해
// 중간에 만든 엔티티
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 빈 생성자 사용 x
public class OrderItem {
    @Id @GeneratedValue
    @Column(name="order_item_id") // PK 이름
    private Long id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="item_id")
    private Item item;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

    //==생성 메서드==//
    //얼마에 몇개 샀는지
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /*
    주문상품 전체 가겨 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
