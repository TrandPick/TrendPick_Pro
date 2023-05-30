package project.trendpick_pro.domain.product.entity.file;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class ProductFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String terminatedFileName; //실제 이미지 업로드에 사용할 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_file_id")
    private ProductFile mainFile;

    //종속
    @OneToMany(mappedBy = "mainFile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductFile> subFiles = new ArrayList<>();

    @OneToOne(mappedBy = "productFile")
    private Product product;

    public ProductFile(String originalFileName, String terminatedFileName) {
        this.originalFileName = originalFileName;
        this.terminatedFileName = terminatedFileName;
    }

    //양방향 맵핑
    public void connectFile(ProductFile subFile){
        this.getSubFiles().add(subFile);
        subFile.setMainFile(this);
    }
}
