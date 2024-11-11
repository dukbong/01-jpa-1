package com.example.jpa_study_1.api;

import com.example.jpa_study_1.domain.Address;
import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.domain.Order;
import com.example.jpa_study_1.domain.OrderStatus;
import com.example.jpa_study_1.repository.OrderRepository;
import com.example.jpa_study_1.repository.OrderSearch;
import com.example.jpa_study_1.repository.order.simplequery.OrderSimpleQueryDto;
import com.example.jpa_study_1.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/***
 * XXToOne 관계일때 JPA 성능 최적화
 * (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 1.1.1
        // 이런식으로 원하는 것들만 가져오는건 성능 최적화에 아무런 영향을 미치지 않는다.
        // 현재 findAllByString()는 JPQL을 사용하기 때문에 Order 엔티티만 로드하고 각 데이터에 접근 시 개별적 쿼리가 발생한다.
        for(Order order : all) {
            Member member = order.getMember(); // 여기까진 DB를 가져오는게 아니라 프록시 객체를 불러오게된다.
            member.getName(); // 이렇게 프록시 객체의 필드에 접근하면 그제서야 로딩을 하여 DB에서 데이터를 가져오게 된다.
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        /***
         * 유명한 N + 1 문제가 발생
         * ORDER(1)를 조회하는데 Member(N) + Delivery(N) 이렇게 해야 하나의 SimpleDto가 완성된다.
         *
         * 만약 10개의 ORDER를 조회한다면? 1 + 10 + 10 총 22개의 쿼리가 발생하게 된다....!!
         */
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .toList();


        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .toList();
        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // LAZY 초기화
            // 영속성 컨텍스트에 만약 해당 Member가 있다면 DB에서 조회하지 않는다.
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }



}
