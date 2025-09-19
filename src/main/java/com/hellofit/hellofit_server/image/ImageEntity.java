package com.hellofit.hellofit_server.image;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import com.hellofit.hellofit_server.global.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

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
    private String targetId;

    @OrderBy("sortOrder ASC")
    private Integer sortOrder;

    public static ImageEntity create(String objectKey, ImageTargetType targetType, String targetId, Integer sortOrder) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.objectKey = objectKey;
        imageEntity.targetType = targetType;
        imageEntity.targetId = targetId;
        imageEntity.sortOrder = sortOrder;

        return imageEntity;

    }
}
