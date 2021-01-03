package jpabook_practice.jpashop_practice.controller;

import jpabook_practice.jpashop_practice.domain.Address;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class MemberDTO {
    @NotEmpty(message = "이름은 필수값입니다.")
    private String name;
    private Long id;
    private Address address;

    public MemberDTO(String name, Long id, jpabook_practice.jpashop_practice.domain.Address address){
        this.name = name;
        this.id = id;
        this.address =address;
    }

}
