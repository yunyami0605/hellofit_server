package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.global.dto.PageResponse;
import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import com.hellofit.hellofit_server.user.exception.UserDuplicateEmailException;
import com.hellofit.hellofit_server.user.exception.UserDuplicateNicknameException;
import com.hellofit.hellofit_server.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /*
    * 유저 생성 서비스 로직
    * */
    public UUID createUser(CreateUserRequestDto request){
        // 1. : 이메일 중복 체크
        checkDuplicateEmail(request.getEmail());

        // 2. : 닉네임 중복 체크
        checkDuplicateNickname(request.getNickname());

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 유저 정보 생성
        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .isPrivacyAgree(request.getIsPrivacyAgree())
                .build();

        // 5. 유저 정보 저장
        return userRepository.save(userEntity).getId();
    }

    /*
     * 유저 id 조회 서비스 로직
     * */
    public UserEntity getUserById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /*
     * 유저 목록 페이지 조회 서비스 로직
     * */
    public PageResponse<UserMappingResponseDto.Detail> getUsersByPage(Pageable pageable) {
        Page<UserMappingResponseDto.Detail> tmp = userRepository.findAll(pageable)
                .map(UserMappingResponseDto.Detail::fromEntity);

        tmp.getContent().forEach(user ->
                log.info("유저: id={}, nickname={}, email={}",
                        user.getId(), user.getNickname(), user.getEmail())
        );

        return PageResponse.from(tmp);
    }

    /*
    * 유저 정보 수정 서비스 로직
    * */
    public UUID updateUser(UUID id, UpdateUserRequestDto request){
        // 1. 유저 id 조회
        UserEntity userEntity = this.getUserById(id);

        // 2. request dto 닉네임 값이 있으면 변경
        if (request.getNickname() != null) {
            // 2-1. : 닉네임 중복 체크
            this.checkDuplicateNickname(request.getNickname());

            userEntity.setNickname(request.getNickname());
        }

        // 3. request dto 비밀번호 값이 있으면 변경
//        if (request.getPassword() != null) {
//            userEntity.setPassword(request.getPassword()); // 실무는 반드시 암호화
//        }

        // 4. 변경된 사항 다시 저장 후 id 반환
        return userRepository.save(userEntity).getId();
    }

    /*
     * 유저 정보 삭제 서비스 로직
     * */
    public UUID deleteUser(UUID id) {
        UserEntity userEntity = this.getUserById(id);
        userRepository.delete(userEntity);

        return userEntity.getId();
    }

    /*
     * 닉네임 중복 체크
     * */
    public void checkDuplicateNickname(String nickname){
        userRepository.findByNickname(nickname).ifPresent((value) -> {
            throw new UserDuplicateNicknameException(value.getId());
        });
    }

    /*
     * 이메일 중복 체크
     * */
    public void checkDuplicateEmail(String email){
        userRepository.findByEmail(email).ifPresent((value) -> {
            throw new UserDuplicateEmailException(value.getId());
        });
    }
}
