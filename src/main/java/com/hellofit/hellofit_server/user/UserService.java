package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.comment.CommentEntity;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.global.dto.PageResponse;
import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.image.ImageService;
import com.hellofit.hellofit_server.image.ImageTargetType;
import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import com.hellofit.hellofit_server.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ImageService imageService;


    /*
     * 유저 생성 서비스 로직
     * */
    @Transactional
    public MutationResponse createUser(CreateUserRequestDto request) {
        // 1. : 이메일 중복 체크
        checkDuplicateEmail(request.getEmail(), "createUser");

        // 2. : 닉네임 중복 체크
        checkDuplicateNickname(request.getNickname(), "createUser");

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 유저 정보 생성
        UserEntity userEntity = UserEntity.create(
            request.getEmail(),
            encryptedPassword,
            request.getNickname(),
            request.getIsPrivacyAgree()
        );

        // 5. 유저 정보 저장
        userRepository.save(userEntity);

        return MutationResponse.of(true);
    }

    /*
     * 유저 id 조회 서비스 로직
     * */
    public UserEntity getUserById(UUID id, String errorPoint) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserException.UserNotFoundException(errorPoint, id));
    }

    /*
     * 유저 목록 페이지 조회 서비스 로직
     * */
    public PageResponse<UserMappingResponseDto.Detail> getUsersByPage(Pageable pageable) {
        Page<UserMappingResponseDto.Detail> tmp = userRepository.findAll(pageable)
            .map(UserMappingResponseDto.Detail::fromEntity);

        return PageResponse.from(tmp);
    }

    /*
     * 유저 정보 수정 서비스 로직
     * */
    @Transactional
    public MutationResponse updateUser(UUID id, UpdateUserRequestDto request) {
        // 1. 유저 id 조회
        UserEntity userEntity = this.getUserById(id, "updateUser");

        // 2. : 닉네임 중복 체크
        if (!userEntity.getNickname()
            .equals(request.getNickname())) {
            this.checkDuplicateNickname(request.getNickname(), "");
        }

        // 2-1. 닉네임 변경
        userEntity.changeNickname(request.getNickname());


        if (request.getProfileImageKey() != null) {
            // 3. 프로필 이미지 변경
            List<ImageEntity> profileImages = this.imageService.getImages(ImageTargetType.UserProfile, id);

            if (!profileImages.isEmpty()) {
                ImageEntity profile = profileImages.get(0);
                this.imageService.deleteImage(profile.getId());
            }

            this.imageService.createImage(request.getProfileImageKey(), ImageTargetType.UserProfile, id, 0);

        }


        // 4. 변경된 사항 다시 저장 후 id 반환
        return MutationResponse.of(true);
    }

    /*
     * 유저 정보 삭제 서비스 로직
     * */
    @Transactional
    public MutationResponse deleteUser(UUID id) {
        UserEntity userEntity = this.getUserById(id, "deleteUser");

        userEntity.softDeleteWithRelations();

        if (userEntity.getRefreshToken() != null) {
            refreshTokenRepository.deleteByUser_Id(id);
        }

        userEntity.getComments()
            .forEach((CommentEntity::softDelete));

        return MutationResponse.of(true);
    }

    /*
     * 닉네임 중복 체크
     * */
    public void checkDuplicateNickname(String nickname, String errorPoint) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException.UserDuplicateNicknameException(errorPoint, nickname);
        }
    }

    /*
     * 이메일 중복 체크
     * */
    public void checkDuplicateEmail(String email, String errorPoint) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException.UserDuplicateEmailException(email, errorPoint);
        }
    }

    /**
     * 유저 존재 여부 -> 없으면 not found 반환
     */
    public void checkUserExists(UUID userId, String errorPoint) {
        if (!userRepository.existsById(userId)) {
            throw new UserException.UserNotFoundException(errorPoint, userId);
        }
    }
}
