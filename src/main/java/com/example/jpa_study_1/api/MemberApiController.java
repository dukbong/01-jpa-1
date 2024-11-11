package com.example.jpa_study_1.api;

import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.service.MemberService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    /***
     * v1과 v2의 차이
     * 1. Entity는 여러곳에서 사용하는 만큼 변경될 확률이 높은데 이로 인해 API 스펙 자체가 달라지기 때문에 API 스펙을 위한 별도의 DTO가 있는것이 좋다.
     * >>> 엔티티가 변경되면 컴파일에서 걸러지기 때문에 확인이 가능하다.
     * >>> 엔티티를 파라미터로 받는 경우 컴파일 단계에서 확인이 불가능하다.
     * 2. 실무에서는 회원가입만 봐도 여러 방법(카카오, 페이스북, 간편 등등)이 있는데 이 모든 것을 엔티티로 처리하는 것은 불가능하다.
     * >>> 어느 곳은 필수, 어느곳은 필수가 아닌 경우가 있기 때문에 사실상 불가능
     * @param request
     * @return
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /***
     * updateMemberV2와 wrongUpdateMemberV2의 차이점은 명령 쿼리 분리 원칙(CQRS)를 지켰냐 안지켰냐의 차이입니다.
     * wrongUpdateMemberV2
     * 1. 반환된 name 값이 실제로 저장된 데이터와 일치하는지 보장되지 않습니다.
     * 2. 업데이트와 조회의 역할이 명확하게 분리되지 않아 코드의 의도가 모호해질 수 있습니다.
     *
     * updateMemberV2
     * 1. 수정(명령 : Command)과 조회(쿼리 : Query)를 분리 함으로써 조회된 데이터를 쓰게 되어 신뢰성을 보장할 수 있습니다.
     * 2. 수정과 조회를 분리하여 해당 코드는 수정하고 조회한다 라는 의도가 명확해집니다.
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/api/v2/members/wrong/{id}")
    public UpdateMemberResponse wrongUpdateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        return new UpdateMemberResponse(id, request.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
         List<Member> findmembers = memberService.findMembers();
         List<MemberDto> collect = findmembers.stream()
                 .map(m -> new MemberDto(m.getName()))
                 .toList();

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}
