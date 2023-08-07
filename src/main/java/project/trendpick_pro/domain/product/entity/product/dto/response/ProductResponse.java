package project.trendpick_pro.domain.product.entity.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse implements Serializable {

    private Long id;
    private String name;
    private String mainCategory;
    private String subCategory;
    private String brand;
    private String description;
    private String mainFile;
    private List<String> subFiles;
    private List<String> sizes;
    private List<String> colors;
    private int price;
    private List<String> tags = new ArrayList<>();
    private int discountRate;
    private int discountedPrice;

    @Builder
    @QueryProjection
    public ProductResponse(Long id, String name, String mainCategory, String subCategory, String brand, String description,
                           String mainFile, List<String> subFiles, List<String> sizes, List<String> colors, int price, List<String> tags, double discountRate, int discountedPrice) {
        this.id = id;
        this.name = name;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.brand = brand;
        this.description = description;
        this.mainFile = mainFile;
        this.subFiles = subFiles;
        this.sizes = sizes;
        this.colors = colors;
        this.price = price;
        this.tags = tags;
        this.discountRate = (int) discountRate;
        this.discountedPrice = discountedPrice;
    }

    public static ProductResponse of (Product product) {
        if (product.getDiscountedPrice() == 0 && product.getDiscountRate() == 0) {
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getTitle())
                    .mainCategory(product.getProductOption().getMainCategory().getName())
                    .subCategory(product.getProductOption().getSubCategory().getName())
                    .brand(product.getProductOption().getBrand().getName())
                    .description(product.getDescription())
                    .mainFile(product.getProductOption().getFile().getFileName())
                    .subFiles(subFiles(product.getProductOption().getFile().getChild()))
                    .sizes(product.getProductOption().getSizes())
                    .colors(product.getProductOption().getColors())
                    .price(product.getProductOption().getPrice())
                    .tags(product.getTags().stream().map(Tag::getName).toList())
                    .build();
        } else {
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getTitle())
                    .mainCategory(product.getProductOption().getMainCategory().getName())
                    .subCategory(product.getProductOption().getSubCategory().getName())
                    .brand(product.getProductOption().getBrand().getName())
                    .description(product.getDescription())
                    .mainFile(product.getProductOption().getFile().getFileName())
                    .subFiles(subFiles(product.getProductOption().getFile().getChild()))
                    .sizes(product.getProductOption().getSizes())
                    .colors(product.getProductOption().getColors())
                    .price(product.getProductOption().getPrice())
                    .tags(product.getTags().stream().map(Tag::getName).toList())
                    .discountedPrice(product.getDiscountedPrice())
                    .discountRate(product.getDiscountRate())
                    .build();
        }
    }

    private static List<String> subFiles(List<CommonFile> subFiles) {
        List<String> tmpList = new ArrayList<>();

        for (CommonFile subFile : subFiles) {
            tmpList.add(subFile.getFileName());
        }
        return tmpList;
    }
}