package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.post.image.PostImageEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer viewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageEntity> postImages = new ArrayList<>();

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    public void addImage(ImageEntity _image, int sortOrder) {
        PostImageEntity mapping = PostImageEntity.builder()
            .post(this)
            .image(_image)
            .sortOrder(sortOrder)
            .build();

        postImages.add(mapping);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        // 조회수 0으로 초기화
        if (viewCount == null) {
            viewCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

//    /** 댓글 */
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CommentEntity> comments = new ArrayList<>();
//
//    /** 북마크 */
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<BookmarkEntity> bookmarks = new ArrayList<>();

//    /** 좋아요 */
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<LikeEntity> likes = new ArrayList<>();

//
//    public void addComment(CommentEntity comment) {
//        comments.add(comment);
//        comment.setPost(this);
//    }
}
