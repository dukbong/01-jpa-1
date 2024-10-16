package com.example.jpa_study_1.domain;

import com.example.jpa_study_1.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    // N:N을 실제로는 쓰지 않는다.
    // tip.실무에서 사용하지 않는 이유는 이렇게 되면 만들어진 중간 테이블에 컬럼을 추가할 수 없다.

    // 중간 테이블을 만들어야 하기 때문에 JoinTable이라는 것을 만들어서 사용해야한다.
    @JoinTable(name = "category_item",
        joinColumns = @JoinColumn(name = "category_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

}
