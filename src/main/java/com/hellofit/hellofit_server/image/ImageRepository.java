package com.hellofit.hellofit_server.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {

    List<ImageEntity> findByTargetTypeAndTargetIdOrderBySortOrderAsc(
        ImageTargetType targetType, String targetId
    );
}
