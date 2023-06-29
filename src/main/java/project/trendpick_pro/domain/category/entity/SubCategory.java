package project.trendpick_pro.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MainCategory mainCategory;

    public SubCategory(String name, MainCategory mainCategory) {
        this.name = name;
        this.mainCategory = mainCategory;
        this.mainCategory.getSubCategories().add(this);
    }

    public void modify(String name) {
        this.name = name;
    }
}
