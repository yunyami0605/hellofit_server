package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="posts")
@Getter @Setter @Builder
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> images = new ArrayList<>();

    public void addImage(ImageEntity _image){
        images.add(_image);
        _image.setPost(this);
    }

    public void removeImage(ImageEntity _image){
        images.remove(_image);
        _image.setPost(null);
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
