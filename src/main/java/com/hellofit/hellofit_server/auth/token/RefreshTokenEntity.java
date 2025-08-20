package com.hellofit.hellofit_server.auth.token;

import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshTokenEntity {

    @Id
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
