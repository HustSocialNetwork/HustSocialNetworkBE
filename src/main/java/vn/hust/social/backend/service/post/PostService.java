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
                        PostDTO postDTO = postDTOMapper.toDTO(post);
                        postRepository.delete(post);
                        return new DeletePostResponse(postDTO);
                } else
                        throw new ApiException(ResponseCode.CANNOT_DELETE_POST);
        }
}
