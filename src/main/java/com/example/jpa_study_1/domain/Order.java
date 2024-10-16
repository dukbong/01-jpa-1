package com.example.jpa_study_1.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간 java 8 이상 부터 LocalDateTime 사용 시 Hibernate에서 자동으로 매핑해준다.

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    // 연관 관계 편의 메소드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //== 생성 메소드 ==//

    /***
     * Order 객체를 만들기 위해서 crateOrder 메소드를 통해서 만들 수 있도록 만들었다.
     * 이러한 과정을 통해 응집력을 높일 수 있다.
     * ::: 응집력을 높이려면 setter 메소드들을 모두 private으로 만들어야 할꺼같지만 강의에서는 public으로 놓았다.
     * @param member
     * @param delivery
     * @param orderItems
     * @return
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//

    /***
     * 주문 취소 :
     * 1. DeliveryStatus가 COMP(주문 취소) 일 경우 에러 발생
     * 2. 현재 주문상태를 CANCEL로 변경
     * 3. OrderItem 객체와 1:N 관계로 양방향 관계를 가졌기 때문에 각 OrderItem 객체에서 취소 되었다는 것을 전파
     * 4. OrderItem 객체와 Item 객체는 1:N 관계이기 때문에 OrderItem에서 Item객체의 addStock(int stock) 비즈니스 로직을 호출하며 OrderItem에 저장되어있는 주문 수량만큼 재고 복구 작업
     */
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
//        for(OrderItem orderItem : this.orderItems) {
//            orderItem.cancel();
//        }
        // 줄이기
        this.orderItems.forEach(OrderItem::cancel);
    }

    //==조회 로직==//

    /***
     * 전체 주문 가격 조회
     * @return
     */
    public int getTotalPrice() {
//        int totalPrice = 0;
//        for(OrderItem orderItem : this.orderItems) {
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;

        // Stream으로 줄이기
        return this.orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }

}
