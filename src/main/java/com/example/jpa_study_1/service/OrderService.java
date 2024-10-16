package com.example.jpa_study_1.service;

import com.example.jpa_study_1.domain.Delivery;
import com.example.jpa_study_1.domain.Member;
import com.example.jpa_study_1.domain.Order;
import com.example.jpa_study_1.domain.OrderItem;
import com.example.jpa_study_1.domain.item.Item;
import com.example.jpa_study_1.repository.ItemRepository;
import com.example.jpa_study_1.repository.MemberRepository;
import com.example.jpa_study_1.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /***
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // Entity 조회
        Member member =memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        // 이때 cascade 설정으로 인해 delivery와 orderItem 및 그 하위 까지 persist 된다.
        // cascade를 사용하면 좋은 곳 :
        // 1. Delivery는 Order에서 사용하기 때문에 사용할 수 있다.
        // 2. OrderItem을 참조하는 곳은 Order 객체 뿐이다. ( OrderItem 객체가 다른 객체를 참조할 수는 있지만 다른 객체가 OrderItem을 참조하는 경우는 현재 없다. )
        orderRepository.save(order);

        return order.getId();
    }

    /***
     * 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // Entity 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    /***
     * 검색
     */
}
