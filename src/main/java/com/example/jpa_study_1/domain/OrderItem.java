package com.example.jpa_study_1.domain;

import com.example.jpa_study_1.domain.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    //== 생성 메소드 ==//

    /***
     * OrderItem 생성시 Order 객체를 매개변수로 받지 않는 이유:
     * Order 객체에서 OrderItem과의 관계 설정 시 cascade = CascadeType.All로 했기 때문에
     * Order 객체가 저장될 때, 연관 된 orderItem들도 자동으로 저장된다.
     * @param item
     * @param orderPrice
     * @param count
     * @return
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        // OrderItem 생성시 바로 재고 감소
        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    // 왜 자기 자신의 필드를 가져오는데 getter 메소드로 가져오지?
    public void cancel() {
        this.getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
