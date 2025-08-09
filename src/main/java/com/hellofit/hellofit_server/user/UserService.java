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

        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .isPrivacyAgree(request.getIsPrivacyAgree())
                .build();

        // 3. 생성된 유저 객체 저장 -> id 반환
        return userRepository.save(userEntity).getId();
    }

    public UserEntity getUserById(UUID id){
//        Optional<UserEntity> optionalUser = userRepository.findById(id);
//
//        if(optionalUser.isPresent()){
//            return optionalUser.get();
//        }else{
//            throw new IllegalArgumentException("Not Found User");
//        }
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not Found User"));
    }

    // 페이지 네이션 조회
    public Page<UserMappingResponseDto.Detail> getUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMappingResponseDto.Detail::fromEntity);
    }

    public UUID updateUser(UUID id, UpdateUserRequestDto request){
        // 1. 유저 id 조회
        UserEntity userEntity = this.getUserById(id);

        // 2. request dto 닉네임 값이 있으면 변경
        if (request.getNickname() != null) {
            userEntity.setNickname(request.getNickname());
        }

        // 3. request dto 비밀번호 값이 있으면 변경
//        if (request.getPassword() != null) {
//            userEntity.setPassword(request.getPassword()); // 실무는 반드시 암호화
//        }

        // 4. 변경된 사항 다시 저장 후 id 반환
        return userRepository.save(userEntity).getId();
    }


    public UUID deleteUser(UUID id) {
        UserEntity userEntity = this.getUserById(id);
        userRepository.delete(userEntity);

        return userEntity.getId();
    }
}
