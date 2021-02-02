package jpabook_practice.jpashop_practice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook_practice.jpashop_practice.controller.MemberForm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id") // PK �̸�
    private Long id;

    // @NotEmpty : @Valid (javax.validation) 의 어노테이션 -
    // 어떤 API 를 쓰는데서는 @NotEmpty 가 필요한곳도 있고 안필요한 곳도 있기 때문에 여기선 사용 x
    private String name; // username 으로 바꾸면 API 스펙 자체가 바껴버림. API 스펙을 위한 별도의 DTO를 만들어서 사용하자.
    // 그냥 그대로 Member로 요청받고 내보내고 하면 나중에 아주 큰 문제가 발생한다. 회원가입 기능 자체가 여러개가 될 수도 있다.
    // API 개발 시 엔티티를 외부로 노출시키지 말자

    @Embedded
    private Address address;

    @JsonIgnore // API 에서 Order와 양방향 연관관계에 잡혀버림 -> 한쪽은 ignore 시켜줘야함
    @OneToMany(mappedBy = "member") // Order.member 와 1:N 매핑
    private List<Order> orders = new ArrayList<>();

    public void changeMember(MemberForm memberForm) {
        this.name = memberForm.getName();
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        this.address = address;
    }
}
