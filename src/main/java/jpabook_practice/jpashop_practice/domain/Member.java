package jpabook_practice.jpashop_practice.domain;

import jpabook_practice.jpashop_practice.controller.MemberForm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id") // PK 이름
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // Order.member 와 1:N 관계
    private List<Order> orders = new ArrayList<>();

    public void changeMember(MemberForm memberForm) {
        this.name = memberForm.getName();
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        this.address = address;
    }
}
