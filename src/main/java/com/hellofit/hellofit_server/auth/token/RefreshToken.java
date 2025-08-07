package com.hellofit.hellofit_server.auth.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String token;

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
