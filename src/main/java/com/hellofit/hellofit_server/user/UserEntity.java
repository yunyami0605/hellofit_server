package com.hellofit.hellofit_server.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 12)
    private String nickname;

    @Column(nullable = false)
    private Boolean isPrivacyAgree;
}