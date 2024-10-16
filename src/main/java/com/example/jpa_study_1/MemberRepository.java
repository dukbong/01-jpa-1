/*
package com.example.jpa_study_1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    // @PersistenceContext를 붙이면 EntityManager를 의존성 주입해준다.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        // 01님 왈 : 커맨드와 쿼리를 분리해라
        // 저장 한 후 가급적 사이드 임펙트를 일으키는 커맨드성이기 때문에 리턴 값을 거의 만들지 않는다.
        // 가급적 가장 작은 것을 반환한다.
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
*/
