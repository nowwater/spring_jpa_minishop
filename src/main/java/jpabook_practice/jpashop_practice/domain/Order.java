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
    @JoinColumn(name="member_id") // FK �̸�
    private Member member;

    // OrderItem.order �� ���� ����
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // cascade �� ���� persist �� ����
    // ��� ��ƼƼ�� �����Ϸ��� ���� persist �������.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    // => access�� ���� �ϴ� ��(���������� ����)�� FK�� �δ°� ������!

    private LocalDateTime orderDate; // �ֹ� �ð�

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // �ֹ� ���� : [ORDER, CANCEL]

    //==���� �޼���==// ���� : ��������� ������ ���⸸ ��ġ�� ��!!!
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){ // ...�� ������ �ѱ� �� ����
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

    //==�������� �޼���==//
    // ����⿡ �����Ͱ� �߰��Ǿ���ϴ� �����. ��Ʈ���ϴ��ʿ� �߰����ִ� ���� ����.
    // Order�� Member
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // Order�� OrderItem
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // Order�� Delivery
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==����Ͻ� ����==//
    /*
    �ֹ� ���
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP) // �̹� ��� �Ϸ�
            throw new IllegalStateException("�̹� ��ۿϷ�� ��ǰ�� ��Ұ� �Ұ����մϴ�.");
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==��ȸ ����==//
    /*
    ��ü �ֹ� ���� ��ȸ
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
