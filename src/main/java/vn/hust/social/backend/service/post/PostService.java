package vn.hust.social.backend.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.dto.PostDTO;
import vn.hust.social.backend.dto.media.CreateMediaRequest;
import vn.hust.social.backend.dto.media.UpdateMediaRequest;
import vn.hust.social.backend.dto.post.create.CreatePostRequest;
import vn.hust.social.backend.dto.post.create.CreatePostResponse;
import vn.hust.social.backend.dto.post.delete.DeletePostResponse;
import vn.hust.social.backend.dto.post.get.GetPostByPostIdResponse;
import vn.hust.social.backend.dto.post.get.GetPostsByUserIdResponse;
import vn.hust.social.backend.dto.post.get.GetPostsOfFollowingResponse;
import vn.hust.social.backend.dto.post.update.UpdatePostRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostResponse;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.media.Media;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.MediaMapper;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.block.BlockRepository;
import vn.hust.social.backend.repository.media.MediaRepository;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.service.friendship.FriendshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostService {

        private final PostRepository postRepository;
        private final MediaRepository mediaRepository;
        private final UserAuthRepository userAuthRepository;
        private final BlockRepository blockRepository;
        private final FriendshipService friendshipService;
        private final UserMapper userMapper;
        private final MediaMapper mediaMapper;

        @Transactional
        public GetPostByPostIdResponse getPostByPostId(String postId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                UUID postID = UUID.fromString(postId);
                Post post = postRepository.findByPostId(postID)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

                if (!canViewPost(userAuth.getUser(), post))
                        throw new ApiException(ResponseCode.CANNOT_VIEW_POST);

                List<MediaDTO> medias = mediaRepository
                                .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                .stream()
                                .map(mediaMapper::toDTO)
                                .toList();
                User user = post.getUser();
                PostDTO postDTO = new PostDTO(
                                post.getPostId(),
                                userMapper.toDTO(user),
                                post.getContent(),
                                post.getStatus(),
                                post.getVisibility(),
                                post.getLikesCount(),
                                post.getCommentsCount(),
                                medias,
                                post.getCreatedAt());

                return new GetPostByPostIdResponse(postDTO);
        }

        @Transactional
        public GetPostsByUserIdResponse getPostsByUserId(String userId, int page, int pageSize, String email) {
                UserAuth viewerUserAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_VIEWER_NOT_FOUND));
                UUID viewerId = viewerUserAuth.getUser().getId();
                UUID ownerId = UUID.fromString(userId);
                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

                Page<Post> posts = postRepository.findPostsByUser(ownerId, viewerId, pageable);

                List<PostDTO> postDTOS = new ArrayList<>();
                for (Post post : posts.getContent()) {
                        List<MediaDTO> medias = mediaRepository
                                        .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                        .stream()
                                        .map(mediaMapper::toDTO)
                                        .toList();
                        User user = post.getUser();
                        PostDTO postDTO = new PostDTO(
                                        post.getPostId(),
                                        userMapper.toDTO(user),
                                        post.getContent(),
                                        post.getStatus(),
                                        post.getVisibility(),
                                        post.getLikesCount(),
                                        post.getCommentsCount(),
                                        medias,
                                        post.getCreatedAt());
                        postDTOS.add(postDTO);
                }

                return new GetPostsByUserIdResponse(postDTOS);
        }

        @Transactional
        public GetPostsByUserIdResponse getAllPosts(int page, int pageSize, String email) {
                UserAuth viewerUserAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_VIEWER_NOT_FOUND));
                UUID viewerId = viewerUserAuth.getUser().getId();

                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

                Page<Post> posts = postRepository.findAllVisibleToViewer(viewerId, pageable);

                List<PostDTO> postDTOS = new ArrayList<>();
                for (Post post : posts.getContent()) {
                        List<MediaDTO> medias = mediaRepository
                                        .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                        .stream()
                                        .map(mediaMapper::toDTO)
                                        .toList();
                        User user = post.getUser();
                        PostDTO postDTO = new PostDTO(
                                        post.getPostId(),
                                        userMapper.toDTO(user),
                                        post.getContent(),
                                        post.getStatus(),
                                        post.getVisibility(),
                                        post.getLikesCount(),
                                        post.getCommentsCount(),
                                        medias,
                                        post.getCreatedAt());
                        postDTOS.add(postDTO);
                }

                return new GetPostsByUserIdResponse(postDTOS);
        }

        @Transactional
        public GetPostsOfFollowingResponse getPostsOfFriends(int page, int pageSize, String email) {
                UserAuth viewerUserAUth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                UUID viewerId = viewerUserAUth.getUser().getId();
                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
                Page<Post> posts = postRepository.findPostsOfFriends(viewerId, pageable);

                List<PostDTO> postDTOS = new ArrayList<>();
                for (Post post : posts.getContent()) {
                        List<MediaDTO> medias = mediaRepository
                                        .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                        .stream()
                                        .map(mediaMapper::toDTO)
                                        .toList();
                        User user = post.getUser();
                        PostDTO postDTO = new PostDTO(
                                        post.getPostId(),
                                        userMapper.toDTO(user),
                                        post.getContent(),
                                        post.getStatus(),
                                        post.getVisibility(),
                                        post.getLikesCount(),
                                        post.getCommentsCount(),
                                        medias,
                                        post.getCreatedAt());
                        postDTOS.add(postDTO);
                }

                return new GetPostsOfFollowingResponse(postDTOS);
        }

        @Transactional
        public CreatePostResponse createPost(CreatePostRequest createPostRequest, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();
                Post post = new Post(user, createPostRequest.content(), createPostRequest.visibility());
                postRepository.saveAndFlush(post);

                for (CreateMediaRequest postMedia : createPostRequest.createPostMediaRequests()) {
                        Media media = new Media(post.getPostId(), MediaTargetType.POST, postMedia.type(),
                                        postMedia.objectKey(),
                                        postMedia.orderIndex());
                        mediaRepository.saveAndFlush(media);
                }

                postRepository.saveAndFlush(post);

                List<MediaDTO> medias = mediaRepository
                                .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                .stream()
                                .map(mediaMapper::toDTO)
                                .toList();
                PostDTO postDTO = new PostDTO(
                                post.getPostId(),
                                userMapper.toDTO(user),
                                post.getContent(),
                                post.getStatus(),
                                post.getVisibility(),
                                post.getLikesCount(),
                                post.getCommentsCount(),
                                medias,
                                post.getCreatedAt());

                return new CreatePostResponse(postDTO);
        }

        @Transactional
        public UpdatePostResponse updatePost(String postId, UpdatePostRequest updatePostRequest, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();
                String userId = userAuth.getUser().getId().toString();

                UUID postID = UUID.fromString(postId);
                Post post = postRepository.findByPostId(postID)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
                String userID = post.getUser().getId().toString();

                if (userID.equals(userId)) {
                        post.setContent(updatePostRequest.content());
                        post.setVisibility(updatePostRequest.visibility());

                        for (UpdateMediaRequest updatePostMediaRequest : updatePostRequest.updatePostMediaRequests()) {
                                if (updatePostMediaRequest.operation() == MediaOperation.DELETE) {
                                        mediaRepository.deleteByObjectKeyAndTargetType(
                                                        updatePostMediaRequest.objectKey(),
                                                        MediaTargetType.POST);
                                }
                                if (updatePostMediaRequest.operation() == MediaOperation.ADD) {
                                        Media media = new Media(post.getPostId(), MediaTargetType.POST,
                                                        updatePostMediaRequest.type(),
                                                        updatePostMediaRequest.objectKey(),
                                                        updatePostMediaRequest.orderIndex());
                                        mediaRepository.saveAndFlush(media);
                                }
                        }
                        postRepository.saveAndFlush(post);

                        List<MediaDTO> medias = mediaRepository
                                        .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                        .stream()
                                        .map(mediaMapper::toDTO)
                                        .toList();
                        PostDTO postDTO = new PostDTO(
                                        post.getPostId(),
                                        userMapper.toDTO(user),
                                        post.getContent(),
                                        post.getStatus(),
                                        post.getVisibility(),
                                        post.getLikesCount(),
                                        post.getCommentsCount(),
                                        medias,
                                        post.getCreatedAt());
                        return new UpdatePostResponse(postDTO);
                } else
                        throw new ApiException(ResponseCode.CANNOT_UPDATE_POST);
        }

        @Transactional
        public DeletePostResponse deletePost(String postId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                String userId = userAuth.getUser().getId().toString();

                UUID postID = UUID.fromString(postId);
                Post post = postRepository.findByPostId(postID)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
                String userID = post.getUser().getId().toString();

                if (userID.equals(userId)) {
                        postRepository.delete(post);
                        List<MediaDTO> medias = mediaRepository
                                        .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                                        .stream()
                                        .map(mediaMapper::toDTO)
                                        .toList();
                        User user = post.getUser();
                        PostDTO postDTO = new PostDTO(
                                        post.getPostId(),
                                        userMapper.toDTO(user),
                                        post.getContent(),
                                        post.getStatus(),
                                        post.getVisibility(),
                                        post.getLikesCount(),
                                        post.getCommentsCount(),
                                        medias,
                                        post.getCreatedAt());

                        return new DeletePostResponse(postDTO);
                } else
                        throw new ApiException(ResponseCode.CANNOT_DELETE_POST);
        }

        public boolean canViewPost(User viewer, Post post) {
                if (blockRepository.existsByBlockerIdAndBlockedId(viewer.getId(), post.getUser().getId())) {
                        return false;
                }

                if (blockRepository.existsByBlockerIdAndBlockedId(post.getUser().getId(), viewer.getId())) {
                        return false;
                }

                if (post.getVisibility() == PostVisibility.PUBLIC)
                        return true;

                if (post.getUser().getId().equals(viewer.getId()))
                        return true;

                if (post.getVisibility() == PostVisibility.FRIENDS) {
                        return friendshipService.isFriend(viewer.getId(), post.getUser().getId());
                }

                return false;
        }
}
