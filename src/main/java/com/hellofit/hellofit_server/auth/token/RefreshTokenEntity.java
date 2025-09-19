package com.hellofit.hellofit_server.auth.token;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
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
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public static RefreshTokenEntity create(String token, UserEntity user) {
        RefreshTokenEntity rtEntity = new RefreshTokenEntity();
        rtEntity.token = token;
        rtEntity.user = user;
        rtEntity.userId = user.getId();
        return rtEntity;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }

//    @Column(nullable = false)
//    private String deviceId; // 클라이언트에서 Localstorage UUID 넘거준걸 디바이스 저장


}
