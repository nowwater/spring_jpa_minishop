package jpabook_practice.jpashop_practice.domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name="delivery_id") // PK
    private Long id;

    // order.delivery를 가져옴.
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)// default: ORDINAL - 1, 2, 3, 4 ... 숫자로 들어감
    // 상태 사이에 뭔가 추가되면 COMP가 2였는데 다른게 2가 되서 망해버림..
    // ORDINAL 절대 쓰면 안됨!!!!!!!!
    // STRING 으로 해야 다른게 추가되어도 순서가 밀리지않음.
    private DeliveryStatus status; // READY(배송준비), COMP(배송)
}
