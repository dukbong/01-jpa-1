package com.example.jpa_study_1.service;

import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
/***
 * @Transactional 우선권 순위:
 * 1. 메소드 레벨 > 2. 클래스 레벨
 *
 * 클래스 레벨에 @Transactional을 적용하면 해당 클래스의 모든 public 메소드에 동일하게 적용된다.
 * 메소드 레벨에 @Transactional이 별도로 지정되었을 경우 클래스 레벨보다 우선권이 있기때문에 클래스에서 지정한 옵션이 적용되지 않는다.
 *
 * @Transactional 옵션 중 readOnley = true로 설정했다면
 * 1. Flush 및 더티 체킹이 발생하지 않는다.
 * 2. 읽기 전용 작업만 수행된다.
 * 3. DB에 따라 다르지만 읽기 전용 Transactional을 효율적으로 처리해 리소스 사용량을 줄일 수 있다.
 *
 * 기본적으로는 readOnly의 옵션 값은 false이며 true인 경우 DB의 상태 변경 (Insert, Update, Delete 등)이 허용되지 않는다.
 * :: 이유는 Flush 및 더티 체킹이 발생하지 않기 때문이다.
 */
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    
    // 회원가입
    @Transactional
    public Long join (Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
