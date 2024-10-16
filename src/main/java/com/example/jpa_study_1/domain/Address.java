package com.example.jpa_study_1.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    /***
     * 값타입의 경우 immutable 하게 만들어져야 한다.
     * 그러므로 생성 할때만 세팅을 해야한다.
     * JPA는 기본적으로 이러한 것들을 Reflection, Proxy 같은 기술을 써야하기 때문에 이 기술들의 필수 사항인 기본 생성자가 있어야한다.
     *
     * 설명 :
     * JPA 스펙상 Entity, Embeddable은 기본 생성자를 public, protected로 설정해야한다.
     * 이러한 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할때 Reflection같은 기술을 사용하기 때문이다.
     */

    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
