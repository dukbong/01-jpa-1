package com.example.jpa_study_1.repository;

import com.example.jpa_study_1.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString (OrderSearch orderSearch) {
        // 정적 쿼리
//        return em.createQuery("select o from Order o join o.member m where m.name = :name and o.status = :status", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 최대 1000건만
//                .getResultList();

        // 동적 쿼리 방법 1 [문자열 가지고 만들기]
        // [절대 실무에서 하지 않음]
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        if(orderSearch.getOrderStatus() != null) {
            jpql += " where";
            isFirstCondition = false;
            jpql += " o.status = :status";
        }

        if(StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.member.name = :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    // 동적 쿼리 방법 2 [JPA에가 지원하는 표준 동적 쿼리 방법 - JPA Criteria]
    // [절대 실무에서는 쓰지 않는다.]
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if(orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        return em.createQuery(cq).setMaxResults(1000).getResultList();
    }

//    동적 쿼리 3. QueryDsl [JPA 사용시 꼭 알아야 하는 것, 실무에서 사용]
//    public List<Order> findAllQueryDsl(OrderSearch orderSearch) {
//        QOrder order = QOrder.order;
//        QMember member=  QMember.member;
//
//        return qeury
//                .select(order)
//                .from(order)
//                .join(order.member, member)
//                .where(statusEq(orderSearch.getOrderStatus()),
//                        nameLike(orderSearch.getMemberName()))
//                .limit(1000)
//                .fetch();
//    }
//    
//    private BooleanExpression statusEq(OrderStatus statusCond) {
//        if(statusCond == null) {
//            return null;
//        }
//        return order.status.eq(statusCOnd);
//    }


    /***
     *  findAllWithMemberDelivery와 findOrderDtos 중에 뭐가 더 좋을까?
     *  이는 서로 장단점이 명확히 나뉜다.
     *  장점 :
     *  findAllWithMemberDelivery의 경우 범용적으로 사용 가능하다.
     *  findOrderDtos는 조회하는 데이터의 양이 적기 때문에 성능면에서 좋다. ( 눈에 띄는 차이는 나지 않는다. )
     *  단점 :
     *  findAllWithMemberDelivery의 경우 데이터 양이 많기 때문에 성능면에서 조금 부족하다. ( 눈에 띄는 차이는 나지 않는다. )
     *  findOrderDtos는 Fit하게 조회하기 때문에 한정적이다.
     *  >> API 스펙에 딱 맞춘 코드가 리포지토리에 들어가는 것은 치명적 단점이다. ( API 스펙이 변경되는 경우 복잡해진다. )
     *  findOrderDtos는 DTO를 반환하기 때문에 해당 데이터를 변경하는 것은 불가능하다.
     */


    public List<Order> findAllWithMemberDelivery() {
        /***
         * N + 1 문제를 해결 할 수 있는 Fetch Join
         * LAZY를 무시하고 연관된 데이터를 하나의 쿼리로 가져올 수 있다.
         */
        return em.createQuery(
                "select o from Order o join fetch o.member m join fetch o.delivery d",
                Order.class
        ).getResultList();
    }

    public List<Order> findAllWithItem() {
        /***
         * Hibernate 6 버전 부터는 자동으로 distinct가 적용된다.
         * Link : https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#hql-distinct
         *
         * 원래 결과는 총 4개가 나와야 하지만 지금은 정상적으로 2개 나온다.
         *
         * ⭐⭐⭐1:N 를 Fetch Join 하는 순간 페이징 처리 하면 성능 적으로 좋지 않다.
         * >> 이유 : limit 이런걸 하는게 아니라 다 메모리에 올린 후 페이징 하기 때문에 잘못한면 outofmemory 나온다.
         */
//        return em.createQuery(
//                "select distinct o from Order o " +
//                        "join fetch o.member m " +
//                        "join fetch o.delivery d " +
//                        "join fetch o.orderItems oi " +
//                        "join fetch oi.item i", Order.class
//        ).getResultList();


        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i", Order.class
        ).setFirstResult(1)
                .setMaxResults(100)
                .getResultList();
    }

//    OrderSimpleQueryRepository로 분리 했음
//
//    public List<OrderSimpleQueryDto> findOrderDtos() {
//        /***
//         * 원하는 데이터만 DTO로 바로 반환하기 때문에 성능 면에서는 더 좋다.
//         */
//        return em.createQuery(
//                "select new com.example.jpa_study_1.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
//                        " from Order o" +
//                        " join o.member m" +
//                        " join o.delivery d", OrderSimpleQueryDto.class
//
//        ).getResultList();
//    }
}
