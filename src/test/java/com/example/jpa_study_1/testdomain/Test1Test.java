package com.example.jpa_study_1.testdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class Test1Test {

    @Autowired Test1Repository test1Repository;

    @Test
    void test() {
        // given
        Test1 test1 = new Test1();
        test1.setName("test1");
        test1Repository.save(test1);
        // when

        // then
    }
  
}