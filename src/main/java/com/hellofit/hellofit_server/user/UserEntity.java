package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.auth.token.RefreshTokenEntity;
import com.hellofit.hellofit_server.comment.CommentEntity;
import com.hellofit.hellofit_server.global.entity.SoftDeletableEntity;
import com.hellofit.hellofit_server.like.LikeEntity;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.user.enums.LoginProvider;
import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class UserEntity extends SoftDeletableEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @Column()
    private String password;

    @NotBlank
    @Column(nullable = false, length = 12)
    private String nickname;

    @NotNull
    @Column(nullable = false)
    private Boolean isPrivacyAgree;

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    private LoginProvider provider;
    /**
     * 1:1 Refresh Token
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshTokenEntity refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeEntity> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfileEntity profile;


    /**
     * 1:N Posts
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    // 이메일 회원가입
    public static UserEntity createEmail(String email, String encodedPassword, String nickname, Boolean isPrivacyAgree) {
        UserEntity user = new UserEntity();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.isPrivacyAgree = isPrivacyAgree;
        user.provider = LoginProvider.LOCAL;
        return user;
    }

    // 소셜 회원가입
    public static UserEntity createSocial(String email, String nickname, Boolean isPrivacyAgree, String socialId, LoginProvider provider) {
        UserEntity user = new UserEntity();
        user.email = email;
        user.password = null; // 소셜은 password 없음
        user.nickname = nickname;
        user.isPrivacyAgree = isPrivacyAgree;
        user.socialId = socialId;
        user.provider = provider;
        return user;
    }

    // 게시글 추가
    public void addPost(PostEntity post) {
        this.posts.add(post);
        post.setUser(this);
    }

    // 댓글 추가
    public void addComment(CommentEntity comment) {
        this.comments.add(comment);
        comment.setUser(this);
    }

    private Set<String> normalizeFoods(List<String> input) {
        if (input == null) return new HashSet<>();
        // 공백 트림, 빈 값 제거, 길이 제한, 중복 제거(Set)
        return input.stream()
                    .map(s -> s == null ? null : s.trim())
                    .filter(s -> s != null && !s.isEmpty())
                    .map(s -> s.length() > 100 ? s.substring(0, 100) : s) // DB length=100 보호
                    .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void changeNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("바꿔야함");
        }
        this.nickname = nickname;
    }

    public void changePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("바꿔야함");
        }
        this.password = encodedPassword;
    }

    // 관계 soft delete
    public void softDeleteWithRelations() {
        this.softDelete();
        this.posts.forEach(PostEntity::softDelete);
        this.comments.forEach(CommentEntity::softDelete);
    }

}
