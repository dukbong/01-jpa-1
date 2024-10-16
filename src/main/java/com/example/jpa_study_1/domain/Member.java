package com.example.jpa_study_1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    // 의미 : 관계의 주인은 Order이며, Order 객체의 Member 속성에 따라 달라진다.
    // 현재 orders는 조회용으로만 사용이 가능하게 된다.
    private List<Order> orders = new ArrayList<>();

}
