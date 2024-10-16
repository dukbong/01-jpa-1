package com.example.jpa_study_1.service;

import com.example.jpa_study_1.domain.Address;
import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.domain.Order;
import com.example.jpa_study_1.domain.OrderStatus;
import com.example.jpa_study_1.domain.item.Book;
import com.example.jpa_study_1.domain.item.Item;
import com.example.jpa_study_1.exception.NotEnoughStockException;
import com.example.jpa_study_1.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveOrder() {
        // given
        Member member = createMember();

        Book book = createBook("시골 JPA",10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order order = orderRepository.findOne(orderId);
        Assertions.assertThat(OrderStatus.ORDER).isEqualTo(order.getStatus());
        Assertions.assertThat(1).isEqualTo(order.getOrderItems().size());
        Assertions.assertThat(20000).isEqualTo(order.getTotalPrice());
        Assertions.assertThat(8).isEqualTo(book.getStockQuantity());
    }

    @Test
    void cancelOrder() {
        // given
        Member member = createMember();
        Item book = createBook("시골 JPA-2", 10000, 10);
        Long orderId = orderService.order(member.getId(), book.getId(), 5);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order order = orderRepository.findOne(orderId);
        Assertions.assertThat(OrderStatus.CANCEL).isEqualTo(order.getStatus());
        Assertions.assertThat(10).isEqualTo(book.getStockQuantity());
    }
    
    @Test
    void itemStockOve() {
        // given
        Member member = createMember();
        Item book = createBook("시골 JPA",10000, 10);
        int orderCount = 11;
        // when & then
        Assertions.assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);
    }

    private Book createBook(String name, int orderPrice, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(orderPrice);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("seoul", "river", "123-123"));
        em.persist(member);
        return member;
    }
}