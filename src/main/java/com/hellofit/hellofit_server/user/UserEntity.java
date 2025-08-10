package com.hellofit.hellofit_server.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor @Builder
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 12)
    private String nickname;

    @NotNull
    @Column(nullable = false)
    private Boolean isPrivacyAgree;
}
