package project.trendpick_pro.domain.common.base.filetranslator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.product.entity.file.ProductFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileTranslator {

    @Value("${file.dir}")
    private String filePath; //저장경로

    //이미지 업로드할때 url경로
    public String getFilePath(String filename) {
        return filePath + filename;
    }

    //단일 멀티파일 들어왔을때 저장하고 반환
    public ProductFile translateFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String terminatedFileName = translateFileName(originalFilename);
        multipartFile.transferTo(new File(getFilePath(terminatedFileName)));

        return ProductFile
                .builder()
                .originalFileName(originalFilename)
                .terminatedFileName(terminatedFileName)
                .build()
                ;
    }

    //파일 여러개를 한 번에 묶어서 변환할때
    public List<ProductFile> translateFileList(List<MultipartFile> MultipartFiles) throws IOException {
        List<ProductFile> fileList = new ArrayList<>();
        for(MultipartFile multipartFile : MultipartFiles){
            if(!multipartFile.isEmpty()){
                fileList.add(translateFile(multipartFile));
            }
        }
        return fileList;
    }

    private String translateFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}