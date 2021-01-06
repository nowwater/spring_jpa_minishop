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

    // order.delivery�� ������.
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)// default: ORDINAL - 1, 2, 3, 4 ... ���ڷ� ��
    // ���� ���̿� ���� �߰��Ǹ� COMP�� 2���µ� �ٸ��� 2�� �Ǽ� ���ع���..
    // ORDINAL ���� ���� �ȵ�!!!!!!!!
    // STRING ���� �ؾ� �ٸ��� �߰��Ǿ ������ �и�������.
    private DeliveryStatus status; // READY(����غ�), COMP(���)
}
