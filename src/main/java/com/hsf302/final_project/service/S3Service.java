package com.hsf302.final_project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Upload file lên S3 và trả về đường link truy cập công khai
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Đổi tên file để không bị trùng (thêm UUID ở đầu)
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        // Đẩy file lên S3
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Trả về đường link (URL) để lưu vào Database SQL Server
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName).key(fileName).build()).toString();
    }

    /**
     * xóa bài giảng và file trên s3
     */
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public String uploadFileWithoutException(MultipartFile file) {

        try {

            // validate
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String originalFilename = file.getOriginalFilename();

            String fileName = UUID.randomUUID() + "_"
                    + originalFilename;

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

            return s3Client.utilities()
                    .getUrl(GetUrlRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build())
                    .toString();

        }

        // lỗi đọc file
        catch (IOException e) {

            log.error(
                    "Cannot read file: {}",
                    file.getOriginalFilename(),
                    e
            );

            throw new RuntimeException(
                    "Cannot read uploaded file",
                    e
            );
        }

        // lỗi AWS S3
        catch (S3Exception e) {

            log.error(
                    "S3 upload failed: {}",
                    e.awsErrorDetails().errorMessage(),
                    e
            );

            throw new RuntimeException(
                    "Upload file to S3 failed",
                    e
            );
        }
        // lỗi khác
        catch (Exception e) {
            log.error("Unexpected error while uploading file", e);
            throw new RuntimeException("Unexpected error while uploading file", e);
        }
    }
}