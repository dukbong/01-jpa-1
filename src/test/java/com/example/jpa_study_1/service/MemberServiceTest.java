package com.example.jpa_study_1.service;

import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("정상적인 회원가입")
    void join() {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        /***
         * insert query가 생성되는 시점은 트랜젝션이 COMMIT하는 시점인데
         * Test 코드에서 @Transactional을 주게 되면 COMMIT이 아닌 ROllback을 하기 때문에
         * 만약 insert 쿼리가 보고 싶다면?
         * 1. @Rollback(false) 준다.
         * 2. EntityManager를 의존성 주입해서 join 메소드 호출 후 flush()를 한다.
         *
         * 추천은 2번 이유는 Test 코드에서 Rollback하지 않았을 경우 불편한 점들이 많다.
         */
        Long memberId = memberService.join(member);

        // then
        entityManager.flush();
        Member findMemberId = memberService.findOne(memberId);
        Assertions.assertThat(member).isEqualTo(findMemberId);
    }
    
    @Test
    @DisplayName("중복된 이름으로 회원가입 예외 발생")
    void duplicatedJoin() {
        // given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        // when & then
        memberService.join(member1);
        Assertions.assertThatThrownBy(() -> memberService.join(member2)).isInstanceOf(IllegalStateException.class);
    }

}