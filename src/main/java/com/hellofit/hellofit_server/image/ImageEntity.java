package com.hellofit.hellofit_server.image;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class ImageEntity extends BaseEntity {

    // s3 object key
    @Column(nullable = false)
    private String objectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImageTargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @OrderBy("sortOrder ASC")
    private Integer sortOrder;

    public static ImageEntity create(String objectKey, ImageTargetType targetType, UUID targetId, Integer sortOrder) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.objectKey = objectKey;
        imageEntity.targetType = targetType;
        imageEntity.targetId = targetId;
        imageEntity.sortOrder = sortOrder;

        return imageEntity;
    }


    public void changeSortOrder(Integer sortOrder) {
        if (sortOrder < 0) {
            throw new IllegalArgumentException("잘못된 데이터입니다.");
        }
        this.sortOrder = sortOrder;
    }
}
