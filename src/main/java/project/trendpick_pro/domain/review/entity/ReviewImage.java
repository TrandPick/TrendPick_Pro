package project.trendpick_pro.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    private String mainFileName;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> subFileNames = new ArrayList<>();

    @OneToOne(mappedBy = "reviewImage", fetch = FetchType.LAZY)
    private Review review;

    public ReviewImage(String mainFileName, Review review){
        this.mainFileName = mainFileName;
        this.review = review;
    }

    public void deleteImage(String filePath){
        for(String subFile : subFileNames){
            File file = new File(filePath + subFile);
            file.delete();
        }
        File file = new File(filePath + mainFileName);
        file.delete();
    }

    public void saveSubFileNames(List<String> subFileNames) {
        this.subFileNames = subFileNames;
    }

    public void changeMainFile(String filePath, String mainFilePath, MultipartFile mainFile) throws IOException {
        File file = new File(filePath + mainFileName);
        file.delete();  //원래 있던거 삭제

        this.mainFileName = mainFilePath;
        mainFile.transferTo(new File(filePath + mainFilePath));
    }

    public void changeSubFile(String filePath, List<String> subFileNames) {
        //음 우선 돌면서 지워야겠지??
        for(String subFile: subFileNames){
            File file = new File(filePath + subFile);
            file.delete();
        }
        this.subFileNames = subFileNames;
    }
}
