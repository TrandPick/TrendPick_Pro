package project.trendpick_pro.domain.product.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.List;

@Data
public class ProductSaveRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String name;

    @NotBlank(message = "내용을 입력해주세요.")
    private String description;

    @NotBlank(message = "메인 카테고리를 정하세요.")
    private String mainCategory;

    @NotBlank(message = "서브 카테고리를 정하세요.")
    private String subCategory;

    @NotBlank(message = "브랜드를 입력해주세요.")
    private String brand;

    @NotBlank(message = "메인 사진을 입력해주세요.")
    private MultipartFile mainFile;

    @NotBlank(message = "서브 사진들을 입력해주세요.")
    private List<MultipartFile> subFiles;

    @NotBlank(message = "가격을 입력해주세요.")
    private int price;

    @NotBlank(message = "수량을 입력해주세요.")
    private int stock;

    @NotBlank(message = "포함될 태그들을 추가해주세요.")
    private List<String> tags;

}
