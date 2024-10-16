package com.example.jpa_study_1.controller;

import com.example.jpa_study_1.domain.Address;
import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/new")
    public String create(@Valid MemberForm memberForm, BindingResult bindingResult) {
        /***
         * @Valid + BingResult 객체를 매개변수로 받는 경우 JSP, Thymeleaf에서는 Model에 담지 않아도
         * Valid를 통해 발견된 문제를 화면에 뿌려줄 수 있다.
         *
         * DB에서 NOT NULL 조건에 있는걸 모두 @Valid로 감싼 후 2차 검증을 통해 좀 더 확실한 데이터를 저장 할 수 있다.
         */

        if (bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }
}
