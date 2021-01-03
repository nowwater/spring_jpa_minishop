package jpabook_practice.jpashop_practice.domain;

import jpabook_practice.jpashop_practice.controller.MemberForm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    public void changeMember(MemberForm memberForm) {
        this.name = memberForm.getName();
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        this.address = address;
    }
}
