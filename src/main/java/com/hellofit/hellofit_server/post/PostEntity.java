package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.comment.CommentEntity;
import com.hellofit.hellofit_server.global.entity.SoftDeletableEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class PostEntity extends SoftDeletableEntity {

    @NotBlank
    @Column(nullable = false, length = 80)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer viewCount = 0;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    public static PostEntity Create(UserEntity user, String title, String content) {
        PostEntity postEntity = new PostEntity();
        postEntity.title = title;
        postEntity.content = content;

        // 유저 연결
        user.addPost(postEntity);

        return postEntity;
    }


    public void changeTitle(String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("잘못된 데이터입니다.");
        }
        this.title = title;
    }

    public void changeContent(String content) {
        if (content.isBlank()) {
            throw new IllegalArgumentException("잘못된 데이터입니다.");
        }
        this.content = content;
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 댓글 연결
    public void addComment(CommentEntity comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }
}
