package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UUID createUser(CreateUserRequestDto request){
        // 1 : 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .isPrivacyAgree(request.getIsPrivacyAgree())
                .build();

        return userRepository.save(user).getId();
    }

    public User getUserById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not Found User"));
    }

    public Page<UserMappingResponseDto.Detail> getUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMappingResponseDto.Detail::fromEntity);
    }

    public UUID updateUser(UUID id, UpdateUserRequestDto request){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not Found User"));

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        if (request.getPassword() != null) {
            user.setPassword(request.getPassword()); // 실무는 반드시 암호화
        }

        return userRepository.save(user).getId();
    }


    public UUID deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);

        return user.getId();
    }
}
