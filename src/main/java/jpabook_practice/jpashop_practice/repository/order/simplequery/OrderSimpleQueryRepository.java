package jpabook_practice.jpashop_practice.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos(){ // new 명령어를 사용해 JPQL 결과를 DTO 로 즉시 변환
        // 재사용성이 안좋음
        return em.createQuery( // OrderSimpleQueryDto() 에 엔티티로 넘기면 엔티티 식별자로 넘어가버림.
                "select new jpabook_practice.jpashop_practice.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
        // DTO 와 바로 매핑되지 않는다.
        // JPA는 기본적으로 엔티티나 Value Object만 반환할 수 있음.
        // 따라서 new 오퍼레이션을 사용해야함
    }
}
