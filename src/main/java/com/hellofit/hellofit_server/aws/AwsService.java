package com.hellofit.hellofit_server.aws;

import com.hellofit.hellofit_server.aws.dto.AwsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsService {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public AwsResponseDto.GetS3PresignedPatchUrl generatePresignedUrl(String originalFileName, String contentType) {
        String key = "uploads/" + UUID.randomUUID() + "_" + originalFileName;

        // 1. PUT Presigned URL 생성 (업로드용)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build();

        PresignedPutObjectRequest presignedPut = s3Presigner.presignPutObject(r -> r
            .signatureDuration(Duration.ofMinutes(5)) // 업로드 5분만 유효
            .putObjectRequest(putObjectRequest)
        );

        // 2. GET Presigned URL 생성 (조회용)
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        PresignedGetObjectRequest presignedGet = s3Presigner.presignGetObject(r -> r
            .signatureDuration(Duration.ofHours(1)) // 다운로드 URL 1시간 유효
            .getObjectRequest(getObjectRequest)
        );

        return AwsResponseDto.GetS3PresignedPatchUrl.of(
            presignedPut.url()
                .toString(),
            key,
            presignedGet.url()
                .toString()
        );
    }

    /**
     * aws s3 presigned get url 조회
     *
     * @param key : 저장된 이미지 키
     */
    public String presignedGetUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        PresignedGetObjectRequest presignedGet = s3Presigner.presignGetObject(r -> r
            .signatureDuration(Duration.ofHours(1)) // 다운로드 URL 1시간 유효
            .getObjectRequest(getObjectRequest)
        );

        return presignedGet.url()
            .toString();
    }

    /**
     * s3 객체 삭제
     *
     * @param key 저장된 객체 key
     */
    public void deleteObject(String key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            DeleteObjectResponse response = s3Client.deleteObject(deleteRequest);
            log.info("Deleted object from S3: key={}, bucket={}", key, bucketName);
        } catch (Exception e) {
            log.error("Failed to delete object from S3. key={}, bucket={}, error={}",
                key, bucketName, e.getMessage(), e);
        }
    }
}
