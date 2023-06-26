//package project.trendpick_pro.global.search.entity;
//
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//import project.trendpick_pro.domain.product.entity.product.Product;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Document(indexName = "trendpick__dev___product_type_1")
//public class ProductSearch {
//
//    @Id
//    private Long id;
//
//    @Field(type = FieldType.Keyword)
//    private String name;
//
//    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
//    private String name_nori;
//
//    @Field(type = FieldType.Keyword)
//    private String mainCategory;
//
//    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
//    private String mainCategory_nori;
//
//    @Field(type = FieldType.Keyword)
//    private String brand;
//
//    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
//    private String brand_nori;
//
//    @Field(type = FieldType.Long)
//    private Long productId;
//
//    @Field(type = FieldType.Integer)
//    private int price;
//
//    @Builder
//    public ProductSearch(String name, String mainCategory, String brand, Long productId, int price) {
//        this.name = name;
//        this.mainCategory = mainCategory;
//        this.brand = brand;
//        this.productId = productId;
//        this.price = price;
//    }
//
//    public static ProductSearch of(Product product) {
//        return ProductSearch.builder()
//                .name(product.getName())
//                .mainCategory(product.getMainCategory().getName())
//                .brand(product.getBrand().getName())
//                .productId(product.getId())
//                .price(product.getPrice())
//                .build();
//    }
//
//    public void modify(Product product) {
//        this.name = product.getName();
//        this.mainCategory = product.getMainCategory().getName();
//        this.brand = product.getBrand().getName();
//        this.productId = product.getId();
//        this.price = product.getPrice();
//    }
//}
