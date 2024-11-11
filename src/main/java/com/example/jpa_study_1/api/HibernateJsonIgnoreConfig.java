package com.example.jpa_study_1.api;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * 실무에서 사용할 일 거의 없음.
 * 이유는 Entity를 노출 시키지 않기 때문이다.
 */
@Configuration
public class HibernateJsonIgnoreConfig {

    // spring boot 3.0 이상
    // implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta'
    @Bean
    Hibernate5JakartaModule hibernate5Module() {
        Hibernate5JakartaModule module =  new Hibernate5JakartaModule();
        // 기본적으로 지연로딩의 경우 아래와 같이 로딩을 시키지 않으면 null값으로 들어간다.
        // 아래와 같이 설정 시 지연 로딩으로 되어있는 모든걸 불러오게 된다.
//        module.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
//        이렇게 강제로 전체 다 로딩 하지 않고 원하는 것만 로딩 시켜서 사용할 수 도 있긴 하다. (1.1.1)
        return module;
    }


    // spring boot 3.0 미만
    // implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5'
//    @Bean
//    Hibernate5Module hibernate5Module() {
//        return new Hibernate5Module();
//    }


}
