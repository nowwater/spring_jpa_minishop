package jpabook_practice.jpashop_practice.repository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook_practice.jpashop_practice.domain.*;
import jpabook_practice.jpashop_practice.domain.Order;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

import static jpabook_practice.jpashop_practice.domain.QMember.member;
import static jpabook_practice.jpashop_practice.domain.QOrder.order;
import static org.springframework.util.StringUtils.hasText;

@Repository
//@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    //QueryDSL -> 컴파일 시점에 오타가 다 잡힌다는 큰 장점
    public List<Order> findAll(OrderSearch orderSearch){
        /*JPAQueryFactory query = new JPAQueryFactory(em); // 위에 생성자 주입방식으로 받아 써도됨. private final JPAQuery~~
        QOrder order = QOrder.order; // 이건 static import 로 없앨 수 있음
        QMember member = QMember.member;*/

        return query.select(order)
                .from(order)
                .join(order.member, member) // order.member를 조인하는데 alias 로 member 를 준다
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression nameLike(String memberName) {
        if(!StringUtils.hasText(memberName)){
            return null;
        }
        return member.name.like(memberName);
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null) { // 이 경우를 처리하기 위해 따로 뺴내서 동적쿼리로 사용
            return null;
        }
        return order.status.eq(statusCond);
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if(hasText(orderSearch.getMemberName())){
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        // 재사용성이 좋음
        return em.createQuery( // 기본 LAZY 로 깔고, 필요한거만 fetch join 으로 객체 그래프를 묶어서 한번에 가져오면 대부분의 성능 문제는 해결됨.
                "select o from Order o" +
                        " join fetch o.member m" + // order를 가져올 때 member 까지 같이 가져옴
                        " join fetch o.delivery d", Order.class // order를 가져올 때 delivery 까지 같이 가져옴
        ).getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery( // DB 에서 distict 는 한 줄이 전부 똑같아야 중복이 제거됨
                "select distinct o from Order o " + // JPA 에서는 ID가 같으면 중복을 제거해줌
                        // - 엔티티가 중복인 경우 중복을 걸러서 컬렉션에 담아준다!! DB에 select 에서 distinct도 붙여줌.
                        "join fetch o.member m " + // 안해주면 order 조회가 4번됨 (orderItem 이 2개씩 있는거가 각각 조인되서)
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i ", Order.class)
                .getResultList();
    }
    // 위에는 쿼리가 한개로 다 JOIN 해서 가져옴, 데이터 중복이 너무 많음(데이터 뻥튀기, 용량이 큼). 페이징이 불가능
    // 밑에는 쿼리 개수가 좀 늘어났지만, 데이터 중복이 없음(데이터 최적화, 용량이 적음). 페이징이 가능.
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery( // DB 에서 distict 는 한 줄이 전부 똑같아야 중복이 제거됨
                "select distinct o from Order o " +
                        "join fetch o.member m " + // ToOne 은 아무리 Fetch join 해도 문제 X
                        "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // 사실 성능 차이는 조회보다는 where 에서 조건 또는 JOIN 에서 많이 나기 떄문에, V3 와 V4는 크게 성능차이가 안남..
    // 얘는 simplequey 라는 최적화 쿼리를 위한 패키지에서 관리함. -> 유지보수성이 좋아짐.   그럼 여기에는 화면에 종속적인 조회로직이 없음
    /*public List<OrderSimpleQueryDto> findOrderDtos(){ // new 명령어를 사용해 JPQL 결과를 DTO 로 즉시 변환
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
    }*/
}
