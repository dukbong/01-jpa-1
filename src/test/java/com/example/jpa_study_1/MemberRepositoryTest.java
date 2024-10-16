/*
package com.example.jpa_study_1;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    // @Transactional이 TestCode에 있으면 테스트 완료 후 Rollback 하게 된다.
    // 하지만 Assertions로 한 검증을 믿지 못하고 DB를 확인하고 싶을때는
    // @Rollback(false)를 통해 @Transactional이 하는 Rollback을 막을 수 있다.
    @Rollback(false)
    void testMember() {
        // given
        Member member = new Member();
        member.setUsername("memberA");
        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);
        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        // findMember == member
        // 이게 되는 이유는 JPA는 같은 Transaction 내에서 작업하는 객체를
        // 영속성 컨테이너에서 관리하므로 항상 같은 객체로 처리하게 된다.
        // 또한 30번째 Line에서 SELECT 쿼리가 나가지 않는 이유는 이미 1차 캐시에 존재하기 때문에 이를 그대로 반환하게 된다.
        Assertions.assertThat(findMember).isEqualTo(member);
    }


}*/
