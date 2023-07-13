package project.trendpick_pro.domain.common.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import project.trendpick_pro.domain.common.file.CommonFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileTranslator {

    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public CommonFile saveFile(MultipartFile multipartFile) throws RuntimeException {
        String translatedFileName = uploadFileToS3(multipartFile);
        return CommonFile.builder().fileName(translatedFileName).build();
    }

    public List<CommonFile> saveFiles(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .filter(multipartFile -> !multipartFile.isEmpty())
                .map(this::saveFile)
                .toList();
    }

    private String uploadFileToS3(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String translatedFileName = translateFileName(originalFilename);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, translatedFileName, multipartFile.getInputStream(), new ObjectMetadata())
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);

            return translatedFileName;
        } catch (IOException e) {
            throw new RuntimeException("File translation failed", e);
        }
    }

    private String translateFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + FILE_EXTENSION_SEPARATOR + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        return originalFilename.substring(pos + 1);
    }
}