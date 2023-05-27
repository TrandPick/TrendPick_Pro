package project.trendpick_pro.domain.product.entity.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.file.CommonFile;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {

    private Long id;
    private String name;
    private String mainCategory;    // Category
    private List<String> subCategory;   // Category
    private String brand;   // Brand
    private String description;
    private String mainFile;
    private List<String> subFiles;
    private int price;
    private int stock;
    private List<Tag> tags = new ArrayList<>();

    @Builder
    @QueryProjection
    public ProductResponse(Long id, String name, String mainCategory, List<String> subCategory, String brand, String description,
                           String mainFile, List<String> subFiles, int price, int stock, List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.brand = brand;
        this.description = description;
        this.mainFile = mainFile;
        this.subFiles = subFiles;
        this.price = price;
        this.stock = stock;
        this.tags = tags;
    }

    public static ProductResponse of (Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .mainCategory(product.getMainCategory())
                .subCategory(product.getSubCategory())
                .brand(product.getBrand())
                .description(product.getDescription())
                .mainFile(product.getCommonFile().getFileName())
                .subFiles(subFiles(product.getCommonFile().getChild()))
                .price(product.getPrice())
                .stock(product.getStock())
                .tags(product.getTags())
                .build();
    }

    private static List<String> subFiles(List<CommonFile> subFiles) {
        List<String> tmpList = new ArrayList<>();

        for (CommonFile subFile : subFiles) {
            tmpList.add(subFile.getFileName());
        }
        return tmpList;
    }
}