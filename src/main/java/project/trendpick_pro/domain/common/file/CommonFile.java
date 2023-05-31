package project.trendpick_pro.domain.common.file;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.product.entity.file.ProductFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class CommonFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName; //파일 업로드명
    private String translatedFileName; //실제 저장 경로, 업로드할때 이 경로로 이미지 불러옴.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CommonFile parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default //이거없으면 리스트 초기화 된다. 붙여야됨
    private List<CommonFile> child = new ArrayList<>();

    //양방향 맵핑
    //parent : 메인파일
    //child : 서브파일
    public void connectFile(CommonFile childFile){
        this.getChild().add(childFile);
        childFile.setParent(this);
    }

    private void setParent(CommonFile parent) {
        this.parent = parent;
    }
}
