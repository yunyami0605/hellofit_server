package com.hellofit.hellofit_server.comment;

import com.hellofit.hellofit_server.global.entity.SoftDeletableEntity;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class CommentEntity extends SoftDeletableEntity {

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<CommentEntity> recomments = new ArrayList<>(); // 답글

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public static CommentEntity create(PostEntity post, UserEntity user, String content, CommentEntity parent) {

        CommentEntity comment = new CommentEntity();
        comment.post = post;
        comment.user = user;
        comment.content = content;
        comment.parent = parent;

        return comment;
    }

    // content 변경
    public void changeContent(String content) {
        this.content = content;
    }

    // 답글 추가
    public void addRecomment(CommentEntity recomment) {
        this.recomments.add(recomment);
        recomment.parent = this;
    }
}
