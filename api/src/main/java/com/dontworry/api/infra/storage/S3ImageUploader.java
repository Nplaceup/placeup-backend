package com.dontworry.api.infra.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dontworry.api.common.exception.CustomException;
import com.dontworry.api.common.constant.ApiResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class S3ImageUploader {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${app.s3.upload-prefix}")
    private String uploadPrefix;
    @Value("${app.s3.presign-expire-minutes}")
    private int expireMinutes;

    public List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty())
            return new ArrayList<>();

        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(this::upload)
                .toList();
    }

    public String upload(MultipartFile file) {
        validateFile(file.getOriginalFilename());

        String key = "images/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest request = new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(request);

            return key;
        } catch (IOException e) {
            throw new CustomException(ApiResponseCode.FAIL_UPLOAD_IMAGE);
        }
    }

    private void validateFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new CustomException(ApiResponseCode.NOT_EXIST_FILE);
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(ApiResponseCode.NOT_EXIST_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");
        if (!allowedExtentionList.contains(extension)) {
            throw new CustomException(ApiResponseCode.INVALID_FILE_EXTENSION);
        }
    }

    public String getImageUrl(String key) {
        return amazonS3.getUrl(bucket, key).toString();
    }


}
