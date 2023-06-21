package project.trendpick_pro.domain.common.file;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName; //실제 저장 경로, 업로드할때 이 경로로 이미지 불러옴.

    //메인파일
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CommonFile parent;

    //서브파일 (만약 메인/서브 유형이 아니라면 그냥 여러개 생성해야한다. 전부 메인으로)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CommonFile> child = new ArrayList<>();

    @Builder
    public CommonFile(String fileName) {
        this.fileName = fileName;
    }

    //양방향 맵핑
    public void connectFile(CommonFile childFile){
        this.getChild().add(childFile);
        childFile.setParent(this);
    }

    private void setParent(CommonFile parent) {
        this.parent = parent;
    }
}