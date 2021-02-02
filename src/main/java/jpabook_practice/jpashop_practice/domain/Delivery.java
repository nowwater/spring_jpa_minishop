package jpabook_practice.jpashop_practice.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore // API 에서 Order와 양방향 연관관계에 잡혀버림 -> 한쪽은 ignore 시켜줘야함
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
