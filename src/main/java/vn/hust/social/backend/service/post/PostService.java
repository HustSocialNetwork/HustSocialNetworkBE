package vn.hust.social.backend.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
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
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.auth.UserAuthRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostService {

        private final PostRepository postRepository;
        private final UserAuthRepository userAuthRepository;
        private final vn.hust.social.backend.service.media.MediaService mediaService;
        private final PostDTOMapper postDTOMapper;
        private final PostPermissionService postPermissionService;
        // Still needed for create logic unless moved? createPost creates User
        // object for Post... wait.
        // Post construction uses User entity. postDTOMapper uses UserMapper.
        // PostService createPost needs to return PostDTO.
        // Yes, but PostService creates a 'Post' entity which requires a 'User' entity.
        // And to return PostDTO we use postDTOMapper.
        // So we don't need UserMapper directly if we only use it for DTOs.
        // Wait, createPostRequest doesn't use UserMapper.
        // Let's check existing logic: `User user = userAuth.getUser(); Post post = new
        // Post(user, ...)`
        // logic ends with `postDTO = new PostDTO(..., userMapper.toDTO(user), ...)`
        // So postDTOMapper handles the user mapping.
        // So UserMapper is NOT needed here anymore!

        // Only PostRepository, UserAuthRepository, MediaService, PostDTOMapper,
        // PostPermissionService.

        @Transactional
        public GetPostByPostIdResponse getPostByPostId(String postId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                UUID postID = UUID.fromString(postId);
                Post post = postRepository.findByPostId(postID)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

                if (!postPermissionService.canViewPost(userAuth.getUser(), post))
                        throw new ApiException(ResponseCode.CANNOT_VIEW_POST);

                return new GetPostByPostIdResponse(postDTOMapper.toDTO(post));
        }

        @Transactional
        public GetPostsByUserIdResponse getPostsByUserId(String userId, int page, int pageSize, String email) {
                UserAuth viewerUserAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_VIEWER_NOT_FOUND));
                UUID viewerId = viewerUserAuth.getUser().getId();
                UUID ownerId = UUID.fromString(userId);
                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

                // Note: Logic for visibility check is improved but here we use repository
                // filter.
                // The repository method 'findPostsByUser' should ideally already filter visible
                // posts or we filter after?
                // Original code: postRepository.findPostsByUser(ownerId, viewerId, pageable);
                // This query likely handles visibility. We assume repository queries are
                // correct.
                Page<Post> posts = postRepository.findPostsByUser(ownerId, viewerId, pageable);

                List<PostDTO> postDTOS = posts.getContent().stream()
                                .map(postDTOMapper::toDTO)
                                .toList();

                return new GetPostsByUserIdResponse(postDTOS);
        }

        @Transactional
        public GetPostsByUserIdResponse getAllPosts(int page, int pageSize, String email) {
                UserAuth viewerUserAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.POST_VIEWER_NOT_FOUND));
                UUID viewerId = viewerUserAuth.getUser().getId();

                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

                Page<Post> posts = postRepository.findAllVisibleToViewer(viewerId, pageable);

                List<PostDTO> postDTOS = posts.getContent().stream()
                                .map(postDTOMapper::toDTO)
                                .toList();

                return new GetPostsByUserIdResponse(postDTOS);
        }

        @Transactional
        public GetPostsOfFollowingResponse getPostsOfFriends(int page, int pageSize, String email) {
                UserAuth viewerUserAUth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                UUID viewerId = viewerUserAUth.getUser().getId();
                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
                Page<Post> posts = postRepository.findPostsOfFriends(viewerId, pageable);

                List<PostDTO> postDTOS = posts.getContent().stream()
                                .map(postDTOMapper::toDTO)
                                .toList();

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
                        mediaService.saveMedia(
                                        post.getPostId(),
                                        MediaTargetType.POST,
                                        postMedia.type(),
                                        postMedia.objectKey(),
                                        postMedia.orderIndex());
                }

                // We fetch it back or just construct DTO?
                // toDTO fetches media from DB. Since we just saved media, it should be fine.
                // However, we might want to ensure consistency.
                // The original code re-fetched media via FindByTargetId... so
                // map(postDTOMapper::toDTO) is correct.

                return new CreatePostResponse(postDTOMapper.toDTO(post));
        }

        @Transactional
        public UpdatePostResponse updatePost(String postId, UpdatePostRequest updatePostRequest, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
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
                                        mediaService.deleteMedia(updatePostMediaRequest.objectKey(),
                                                        MediaTargetType.POST);
                                }
                                if (updatePostMediaRequest.operation() == MediaOperation.ADD) {
                                        mediaService.saveMedia(
                                                        post.getPostId(),
                                                        MediaTargetType.POST,
                                                        updatePostMediaRequest.type(),
                                                        updatePostMediaRequest.objectKey(),
                                                        updatePostMediaRequest.orderIndex());
                                }
                        }
                        postRepository.saveAndFlush(post);

                        return new UpdatePostResponse(postDTOMapper.toDTO(post));
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
                        // Mapping BEFORE delete? Original code deleted first, then mapped.
                        // Wait, original code: postRepository.delete(post); then fetched media.
                        // If post is deleted, can we fetch media?
                        // Usually if there is CASCADE delete, media is gone.
                        // If manual delete, existing code works.
                        // But if we delete post, `postDTOMapper.toDTO(post)` might fail if it relies on
                        // lazy loading or if we want to return the state BEFORE delete.
                        // Original code: delete(post), then findMediaByTargetId...
                        // If Cascade is ON, media is gone.
                        // In original code lines 286-291: delete(post);
                        // mediaRepository.findByTargetId...
                        // If this worked, it implies no cascade or media remains.
                        // Let's stick to original order but capture state if needed.
                        // Actually, `toDTO` implementation fetches media by `post.getPostId()`.
                        // If we delete post, we can still call `toDTO(post)` AS LONG AS media is not
                        // deleted.
                        // If Media is deleted by cascade, `toDTO` will return empty media list.
                        // Original code fetched media after delete.

                        // To be safe and better: map first, then delete.
                        PostDTO postDTO = postDTOMapper.toDTO(post);
                        postRepository.delete(post);
                        return new DeletePostResponse(postDTO);
                } else
                        throw new ApiException(ResponseCode.CANNOT_DELETE_POST);
        }
}
