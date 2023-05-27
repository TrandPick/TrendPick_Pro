package project.trendpick_pro.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.dto.request.ProductSaveRequest;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "main-category_id")
    private String mainCategory;    // Category

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "sub-category_id")
    @ElementCollection      // 이거 빨간줄 때문에 예비로 추가한거라 보고 추가하시면 안되요!!!
    private List<String> subCategory = new ArrayList<>();   // Category


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "brand_id")
    private String brand;   // Brand

    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "common_file_id")
    private CommonFile commonFile;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();

    @Builder
    public Product(String name, String mainCategory, List<String> subCategory, String brand,
                   String description, CommonFile commonFile, int price, int stock, List<Tag> tags) {
        this.name = name;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.brand = brand;
        this.description = description;
        this.commonFile = commonFile;
        this.price = price;
        this.stock = stock;
        this.tags = tags;
    }

    public static Product of(ProductSaveRequest request) {
        return Product.builder()
                .name(request.getName())
                .mainCategory(request.getMainCategory())
                .subCategory(request.getSubCategory())
                .brand(request.getBrand())
                .description(request.getDescription())
                .commonFile(makeFiles(request.getMainFile(), request.getSubFiles()))
                .price(request.getPrice())
                .stock(request.getStock())
                .tags(request.getTags())
                .build();
    }

    public void update(ProductSaveRequest request) {

        // 여기서 파일 지지고 볶고 할 예정
    }

    private static CommonFile makeFiles(MultipartFile mainFile, List<MultipartFile> subFiles) {
        return null;

        // 여기서 파일 지지고 볶고 할 예정
    }
}
