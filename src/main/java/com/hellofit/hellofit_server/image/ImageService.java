package com.hellofit.hellofit_server.image;

import com.hellofit.hellofit_server.aws.AwsService;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.image.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final AwsService awsService;

    /**
     * create image
     *
     * @param objectKey  s3 object key
     * @param targetType 이미지 도메인 타입
     * @param targetId   도메인 id
     * @param sortOrder  출력순
     */
    @Transactional
    public ImageEntity createImage(String objectKey, ImageTargetType targetType, String targetId, Integer sortOrder) {
        ImageEntity imageEntity = ImageEntity.create(objectKey, targetType, targetId, sortOrder);
        return imageRepository.save(imageEntity);
    }

    /**
     * target id의 이미지 가져오기 (sort 순서 보장)
     */
    public List<ImageEntity> getImages(ImageTargetType targetType, String targetId) {
        return imageRepository.findByTargetTypeAndTargetIdOrderBySortOrderAsc(targetType, targetId);
    }

    /**
     * image 조회
     */
    public ImageEntity getImage(UUID id, String errorPoint) {
        return this.imageRepository.findById(id)
            .orElseThrow(() -> new ImageException.ImageNotFoundException(errorPoint, id));
    }

    /**
     * 이미지 삭제
     */
    public MutationResponse deleteImage(UUID id) {
        // 1. 이미지 조회
        ImageEntity imageEntity = getImage(id, "deleteImage");

        // 2. aws 삭제
        this.awsService.deleteObject(imageEntity.getObjectKey());

        // 3. db image 정보 삭제
        this.imageRepository.deleteById(id);

        return MutationResponse.of(true);
    }
}
