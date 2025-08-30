package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.aws.AwsService;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.global.exception.CommonException;
import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.image.dto.ImageResponseDto;
import com.hellofit.hellofit_server.post.dto.PostRequestDto;
import com.hellofit.hellofit_server.post.dto.PostResponseDto;
import com.hellofit.hellofit_server.post.exception.PostException;
import com.hellofit.hellofit_server.post.image.PostImageEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 게시글 서비스 로직
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AwsService awsService;

    // 유저 본인 게시글 목록 조회
    public List<PostResponseDto.Summary> getPostsByUser(UserEntity user) {
        return postRepository.findByUserId(user.getId())
            .stream()
            .map((_posts) -> {
                    List<String> presignedImages = _posts.getPostImages()
                        .stream()
                        .map((_image) ->
                            awsService.presignedGetUrl(_image.getImage()
                                .getObjectKey())
                        )
                        .toList();

                    return PostResponseDto.Summary.from(_posts, presignedImages);
                }
            )
            .toList();
    }

    // 게시글 목록 조회
    public List<PostResponseDto.SummaryList> getPosts() {

        return postRepository.findAll()
            .stream()
            .map((_posts) -> {
                    List<String> presignedImages = _posts.getPostImages()
                        .stream()
                        .map((_image) ->
                            awsService.presignedGetUrl(_image.getImage()
                                .getObjectKey())
                        )
                        .toList();

                    return PostResponseDto.SummaryList.from(_posts, presignedImages);
                }
            )
            .toList();
    }

    /**
     * 게시글 조회
     */
    public PostResponseDto.Summary getPost(UUID id) {
        return postRepository.findById(id)
            .map((_post) -> {

                List<String> presignedImages = _post.getPostImages()
                    .stream()
                    .map((_image) ->
                        awsService.presignedGetUrl(_image.getImage()
                            .getObjectKey())
                    )
                    .toList();

                return PostResponseDto.Summary.from(_post, presignedImages);
            })
            .orElseThrow(() -> new PostException.NotFound("PostSevice -> getPost", id.toString()));
    }

    /**
     * 게시글 검색
     *
     * @param keyword : 검색 키워드
     */
    public List<PostResponseDto.Summary> getSearchPosts(String keyword) {
        return postRepository.findByTitleContaining(keyword)
            .stream()
            .map((_post) -> {
                List<String> presignedImages = _post.getPostImages()
                    .stream()
                    .map((_image) ->
                        awsService.presignedGetUrl(_image.getImage()
                            .getObjectKey())
                    )
                    .toList();

                return PostResponseDto.Summary.from(_post, presignedImages);
            })
            .toList();
    }

    /*
     * 게시글 등록 서비스 로직
     * */
    @Transactional
    public MutationResponse createPost(
        PostRequestDto.Create request,
        UserEntity user
    ) {
        // 1. post 객체 생성
        PostEntity post = PostEntity.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .user(user)
            .build();

        // 2. 이미지 생성 후, 게시글 연결
        IntStream.range(0, request.getImageKeys()
                .size())
            .forEach((i) -> {
                String key = request.getImageKeys()
                    .get(i);

                ImageEntity image = ImageEntity
                    .builder()
                    .objectKey(key)
                    .build();

                post.addImage(image, i);
            });

        // 3. 저장
        postRepository.save(post);

        return MutationResponse.builder()
            .id(post.getId())
            .build();
    }

    /*
     * 게시글 수정 서비스 로직
     * */
    @Transactional
    public MutationResponse updatePost(UUID userId, UUID postId, PostRequestDto.Update requestDto) {
        // 1. 게시글 조회
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException.NotFound("update post", postId.toString()));

        // 2. 작성자인지 검증
        if (!post.getUser()
            .getId()
            .equals(userId)) {
            throw new CommonException.Forbidden("updatePost", userId.toString());
        }

        // 3. 게시글 수정 데이터 입력
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        // 4. db 이미지 가져오기
        List<PostImageEntity> currentImages = post.getPostImages();
        Set<String> currentKeys = currentImages.stream()
            .map((_image) -> _image.getImage()
                .getObjectKey())
            .collect(Collectors.toSet());

        // 5. request 이미지 중복 제거
        List<String> newKeys = requestDto.getImageKeys();
        Set<String> newKeySet = new HashSet<>(newKeys);

        // 6. 수정 요청에 없는 이미지들 제거 (orphan = true)
        currentImages.removeIf(img -> !newKeySet.contains(img.getImage()
            .getObjectKey()));

        // 7. 수정 요청에 새로 추가된 이미지 연결
        IntStream.range(0, newKeys.size())
            .forEach(i -> {
                String key = newKeys.get(i);
                if (!currentKeys.contains(key)) {
                    // 현재 키 목록 중, 없으면 새로 생성 후 추가
                    ImageEntity newImage = ImageEntity.builder()
                        .objectKey(key)
                        .build();

                    post.addImage(newImage, i);
                }
            });


        // 8. 요청 순서에 따라 이미지 순서 재정렬
        IntStream.range(0, newKeys.size())
            .forEach(i -> {
                String key = newKeys.get(i);

                currentImages.stream()
                    .filter(img -> img.getImage()
                        .getObjectKey()
                        .equals(key))
                    .findFirst()
                    .ifPresent(img -> img.setSortOrder(i));
            });

        return MutationResponse.builder()
            .id(postId)
            .build();
    }

    /*
     * 게시글 수정 전 데이터 조회 로직
     * */
    public PostResponseDto.PatchData getPatchPostOne(UUID id) {
        PostEntity post = postRepository.findById(id)
            .orElseThrow(() -> new PostException.NotFound("getPatchPostOne", id.toString()));

        List<ImageResponseDto.DataBeforeMutation> images = post.getPostImages()
            .stream()
            .map((_images) -> {
                String objectKey = _images.getImage()
                    .getObjectKey();

                String presignedUrl = awsService.presignedGetUrl(objectKey);

                return ImageResponseDto.DataBeforeMutation.fromEntity(objectKey, presignedUrl);
            })
            .toList();

        return PostResponseDto.PatchData.fromEntity(post, images);
    }
}
