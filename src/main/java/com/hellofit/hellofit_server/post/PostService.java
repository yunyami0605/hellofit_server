package com.hellofit.hellofit_server.post;

import com.hellofit.hellofit_server.aws.AwsService;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.global.exception.CommonException;
import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.image.ImageRepository;
import com.hellofit.hellofit_server.image.ImageService;
import com.hellofit.hellofit_server.image.ImageTargetType;
import com.hellofit.hellofit_server.image.dto.ImageResponseDto;
import com.hellofit.hellofit_server.like.LikeRepository;
import com.hellofit.hellofit_server.like.LikeTargetType;
import com.hellofit.hellofit_server.post.dto.PostRequestDto;
import com.hellofit.hellofit_server.post.dto.PostResponseDto;
import com.hellofit.hellofit_server.post.exception.PostException;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final AwsService awsService;
    private final ImageService imageService;
    private final UserService userService;
    private final ImageRepository imageRepository;

    // 유저 본인 게시글 목록 조회

    /**
     * 유저 본인 게시글 목록 조회
     *
     * @param userId   user id
     * @param cursorId createdAt 기준 cursor 방식
     * @param size     조회 사이즈
     */
    public CursorResponse<PostResponseDto.SummaryList> getPostsByUser(UUID userId, LocalDateTime cursorId, int size) {
        // 1. size + 1개 페이지 요청
        Pageable pageable = PageRequest.of(0, size + 1);

        // 2. cursorId 기준으로 size + 1개 페이지 조회
        List<PostEntity> posts = this.postRepository.findByUserIdWithCursor(userId, cursorId, pageable);

        // 3. db로 조회한 posts 갯수가 11개이면 hasNext = true -> 기존 요청 size로 array 분리
        boolean hasNext = posts.size() > size;
        List<PostEntity> content = hasNext ? posts.subList(0, size) : posts;

        // 4. 각 게시글에 대한 presigned url 추가
        List<PostResponseDto.SummaryList> items = content
            .stream()
            .map((_posts) -> {
                    List<ImageEntity> imageEntities = this.imageService.getImages(ImageTargetType.POST, _posts.getId());

                    List<String> presignedImages = imageEntities
                        .stream()
                        .map((_image) -> {
                                String key = _image.getObjectKey();

                                if (key == null || key.trim()
                                    .isEmpty()) {
                                    return null;
                                }
                                return awsService.presignedGetUrl(key);
                            }
                        )
                        .toList();

                    int likeCount = likeRepository.countByTargetTypeAndTargetId(
                        LikeTargetType.POST, _posts.getId()
                    );

                    return PostResponseDto.SummaryList.from(_posts, presignedImages, likeCount);
                }
            )
            .toList();

        // 5. items가 빈 배열이면 nextCursor = null, 아니면 마지막 cursorId 설정
        String nextCursor = items.isEmpty()
            ? null
            : items.get(items.size() - 1)
            .getCreatedAt()
            .toString();

        // 6. cursor 응답 형성 후 반환
        return CursorResponse.<PostResponseDto.SummaryList>builder()
            .items(items)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public CursorResponse<PostResponseDto.SummaryList> getPosts(LocalDateTime cursorId, int size) {
        // 1. size + 1개 페이지 요청
        Pageable pageable = PageRequest.of(0, size + 1);

        // 2. cursorId로 size + 1개 페이지 조회
        List<PostEntity> posts = postRepository.findPostsByCursor(cursorId, pageable);

        // 3. 다음 게시글 있는지 체크
        boolean hasNext = posts.size() > size;
        List<PostEntity> contents = hasNext ? posts.subList(0, size) : posts;

        // 4. 게시글 마다 signedurl 추가
        List<PostResponseDto.SummaryList> items = contents
            .stream()
            .map((_posts) -> {
                    List<String> presignedImages = this.imageService.getImages(ImageTargetType.POST, _posts.getId())
                        .stream()
                        .map((_image) -> {
                                String key = _image.getObjectKey();

                                if (key == null || key.trim()
                                    .isEmpty()) {
                                    return null;
                                }

                                return awsService.presignedGetUrl(key);
                            }
                        )
                        .toList();

                    int likeCount = likeRepository.countByTargetTypeAndTargetId(
                        LikeTargetType.POST, _posts.getId()
                    );

                    return PostResponseDto.SummaryList.from(_posts, presignedImages, likeCount);
                }
            )
            .toList();

        // 5. nextCursor 추가
        String nextCursor = items.isEmpty()
            ? null
            : items.get(items.size() - 1)
            .getCreatedAt()
            .toString();

        return CursorResponse.<PostResponseDto.SummaryList>builder()
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .items(items)
            .build();
    }

    /**
     * 게시글 조회 (헬퍼 메서드)
     */
    public PostEntity getPostById(UUID id, String errorPoint) {
        return postRepository.findById(id)
            .orElseThrow(() -> new PostException.NotFound(errorPoint, id));
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostResponseDto.Summary getPostOne(UUID id) {
        PostEntity postEntity = this.getPostById(id, "PostService > getPostOne");

        postEntity.increaseViewCount();

        List<String> presignedGetKey = this.imageService.getImages(ImageTargetType.POST, id)
            .stream()
            .map((_image) ->
                awsService.presignedGetUrl(_image
                    .getObjectKey())
            )
            .toList();


        return PostResponseDto.Summary.from(postEntity, presignedGetKey);
    }

    /**
     * 게시글 검색
     *
     * @param keyword : 검색 키워드 (null 또는 공백이면 전체 게시글 조회)
     */
    public List<PostResponseDto.Summary> getSearchPosts(String keyword) {
        List<PostEntity> posts;

        if (keyword == null || keyword.isBlank()) {
            // keyword가 없으면 전체 게시글 조회
            posts = postRepository.findAll();
        } else {
            // keyword가 있으면 제목 검색
            posts = postRepository.findByTitleContaining(keyword);
        }

        return posts.stream()
            .map(post -> {
                List<String> presignedImages = imageService.getImages(ImageTargetType.POST, post.getId())
                    .stream()
                    .map(img -> awsService.presignedGetUrl(img.getObjectKey()))
                    .toList();

                return PostResponseDto.Summary.from(post, presignedImages);
            })
            .toList();
    }


    /*
     * 게시글 등록 서비스 로직
     * */
    @Transactional
    public MutationResponse createPost(
        PostRequestDto.Create request,
        UUID userId
    ) {
        // 1. 작성자 조회
        UserEntity author = this.userService.getUserById(userId, "PostService > createPost");

        // 2. post 객체 생성
        PostEntity post = PostEntity.Create(
            author,
            request.getTitle(),
            request.getContent()
        );

        // 3. 이미지 생성 후, 게시글 연결
        IntStream.range(0, request.getImageKeys()
                .size())
            .forEach((i) -> {
                String key = request.getImageKeys()
                    .get(i);

                this.imageService.createImage(key, ImageTargetType.POST, post.getId(), i);
            });

        // 4. 저장
        postRepository.save(post);

        return MutationResponse.builder()
            .success(true)
            .build();
    }

    /*
     * 게시글 수정 서비스 로직
     * */
    @Transactional
    public MutationResponse updatePost(UUID userId, UUID postId, PostRequestDto.Update requestDto) {
        // 1. 게시글 조회
        PostEntity postEntity = this.getPostById(postId, "PostService > updatePost");

        // 2. 작성자인지 검증
        if (!postEntity.getUser()
            .getId()
            .equals(userId)) {
            throw new CommonException.Forbidden("updatePost", userId.toString());
        }

        // 3. 게시글 수정 데이터 입력
        postEntity.changeTitle(requestDto.getTitle());
        postEntity.changeContent(requestDto.getContent());

        // 4. db 이미지 가져오기
        List<ImageEntity> currentImages = this.imageService.getImages(ImageTargetType.POST, postEntity.getId());
        Set<String> currentKeys = currentImages.stream()
            .map((_image) -> _image.getObjectKey())
            .collect(Collectors.toSet());

        // 5. request 이미지 중복 제거
        List<String> newKeys = requestDto.getImageKeys();
        Set<String> newKeySet = new HashSet<>(newKeys);

        // 6. 요청에 없는 s3 객체 및 이미지 제거
        currentImages.forEach((_image) -> {
            String _objectKey = _image.getObjectKey();

            if (!newKeySet.contains(_objectKey)) {
                // db 이미지 및 s3 객체 삭제
                this.imageService.deleteImage(_image.getId());
            }
        });

        // 7. 수정 요청에 새로 추가된 이미지 생성
        IntStream.range(0, newKeys.size())
            .forEach(i -> {
                String key = newKeys.get(i);
                if (!currentKeys.contains(key)) {
                    // 현재 키 목록 중, 없으면 새로 생성 후 추가
                    this.imageService.createImage(key, ImageTargetType.POST, postId, i);
                }
            });


        // 8. 요청 순서에 따라 이미지 순서 재정렬
        IntStream.range(0, newKeys.size())
            .forEach(i -> {
                String key = newKeys.get(i);

                currentImages.stream()
                    .filter(img -> img
                        .getObjectKey()
                        .equals(key))
                    .findFirst()
                    .ifPresent(img -> img.changeSortOrder(i));
            });

        return MutationResponse.of(true);
    }

    /**
     * 게시글 한개 삭제 서비스 로직
     *
     * @param authorId 작성자 id
     * @param id       게시글 id
     */
    @Transactional
    public MutationResponse deletePostOne(UUID authorId, UUID id) {
        // 1. 게시글 데이터 조회
        PostEntity post = this.getPostById(id, "PostService > deletePostOne");

        // 2. 작성자인지 체크
        if (!post.getUser()
            .getId()
            .equals(authorId)) {
            throw new CommonException.Forbidden("PostService -> deletePostOne", authorId.toString());
        }

        // 3. s3 객체 제거
        this.imageService.deleteImageByTargetTypeAndTargetId(ImageTargetType.POST, id);

        // 4. 삭제
        postRepository.delete(post);

        return MutationResponse.of(true);
    }

    /*
     * 게시글 수정 전 데이터 조회 로직
     * */
    public PostResponseDto.PatchData getPatchPostOne(UUID id) {
        // 1. 게시글 조회
        PostEntity post = this.getPostById(id, "PostService > getPatchPostOne");

        // 2. aws s3에 presignedUrl 요청
        List<ImageResponseDto.DataBeforeMutation> images = this.imageService.getImages(ImageTargetType.POST, id)
            .stream()
            .map((_images) -> {
                String objectKey = _images.getObjectKey();

                String presignedUrl = awsService.presignedGetUrl(objectKey);

                // 최종 응답 데이터에 통합
                return ImageResponseDto.DataBeforeMutation.fromEntity(objectKey, presignedUrl);
            })
            .toList();

        return PostResponseDto.PatchData.fromEntity(post, images);
    }
}
