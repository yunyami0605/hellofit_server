package com.hellofit.hellofit_server.auth.token;

import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", columnDefinition = "CHAR(36)")
    private UserEntity user;

    @Column(nullable = false, length = 255)
    private String token;

    public static RefreshTokenEntity create(String token, UserEntity user) {
        RefreshTokenEntity rtEntity = new RefreshTokenEntity();
        rtEntity.user = user;
        rtEntity.token = token;
        return rtEntity;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
