package project.trendpick_pro.domain.common.file;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private String FileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CommonFile parent;

    @OneToMany(mappedBy = "parent")
    private List<CommonFile> child = new ArrayList<>();

    // 여기서 파일 생성 삭제 지지고 볶고 할예정

}
