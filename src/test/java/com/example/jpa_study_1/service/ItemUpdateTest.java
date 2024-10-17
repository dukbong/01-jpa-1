package com.example.jpa_study_1.service;

import com.example.jpa_study_1.domain.item.Book;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    void updateTest() {

        Book book = em.find(Book.class, 1L);
        // TX
        book.setName("test");

        // 변경감지 == dirty checking
        // dirty checking은 flush 하는 시점에서 일어난다.
        // TX commit
    }
}

/***
 * JPA 업데이트 쿼리가 나가는 이유 ( dirty checking )
 * 1.
 */
