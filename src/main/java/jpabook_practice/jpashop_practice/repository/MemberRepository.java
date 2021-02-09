package jpabook_practice.jpashop_practice.repository;

import jpabook_practice.jpashop_practice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> { // 타입, PK(id)

    // 이렇게만 지정해놔도 메서드 이름이 정해진 규칙에 따라서
    // select m from Member m where m.name = ? 으로 JPQL을 만들어버림림
   List<Member> findByName(String name);
}
