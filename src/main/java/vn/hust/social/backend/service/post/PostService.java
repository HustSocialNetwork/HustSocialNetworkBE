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
import vn.hust.social.backend.dto.post.create.CreatePostMediaRequest;
import vn.hust.social.backend.dto.post.create.CreatePostRequest;
import vn.hust.social.backend.dto.post.create.CreatePostResponse;
import vn.hust.social.backend.dto.post.delete.DeletePostResponse;
import vn.hust.social.backend.dto.post.get.GetPostByPostIdResponse;
import vn.hust.social.backend.dto.post.get.GetPostsByUserIdResponse;
import vn.hust.social.backend.dto.post.get.GetPostsOfFollowingResponse;
import vn.hust.social.backend.dto.post.update.UpdatePostMediaRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostResponse;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.post.PostMedia;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.PostMapper;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.service.friendship.FriendshipService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Service
public class PostService {

    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final FriendshipService friendshipService;
    private final PostMapper postMapper;

    @Transactional
    public GetPostByPostIdResponse getPostByPostId (String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

        if (!canViewPost(userAuth.getUser(), post)) throw new ApiException(ResponseCode.CANNOT_VIEW_POST);

        PostDTO postDTO = postMapper.toDTO(post);

        return new GetPostByPostIdResponse(postDTO);
    }

    @Transactional
    public GetPostsByUserIdResponse getPostsByUserId(String userId, int page, int pageSize, String email) {
        UserAuth viewerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.POST_VIEWER_NOT_FOUND));
        UUID viewerId = viewerUserAuth.getUser().getId();
        UUID ownerId =  UUID.fromString(userId);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findPostsByUser(ownerId, viewerId, pageable);
        List<PostDTO> postDTOS = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        return new GetPostsByUserIdResponse(postDTOS);
    }

    @Transactional
    public GetPostsByUserIdResponse getAllPosts(int page, int pageSize, String email) {
        UserAuth viewerUserAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.POST_VIEWER_NOT_FOUND));
        UUID viewerId = viewerUserAuth.getUser().getId();

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        // Lấy tất cả post mà viewer có quyền xem
        Page<Post> posts = postRepository.findAllVisibleToViewer(viewerId, pageable);

        List<PostDTO> postDTOS = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        return new GetPostsByUserIdResponse(postDTOS);
    }

    @Transactional
    public GetPostsOfFollowingResponse getPostsOfFriends(int page, int pageSize, String email) {
        UserAuth viewerUserAUth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID viewerId = viewerUserAUth.getUser().getId();
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findPostsOfFriends(viewerId, pageable);
        List<PostDTO> postDTOS = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        return new GetPostsOfFollowingResponse(postDTOS);
    }

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest createPostRequest, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        Post post = new Post(user, createPostRequest.content(), createPostRequest.visibility());

        for (CreatePostMediaRequest postMedia : createPostRequest.createPostMediaRequests()) {
            PostMedia postmedia = new PostMedia(post, postMedia.type(), postMedia.objectKey(), postMedia.orderIndex());
            post.getMediaList().add(postmedia);
        }

        postRepository.saveAndFlush(post);
        PostDTO postDTO = postMapper.toDTO(post);

        return new CreatePostResponse(postDTO);
    }

    @Transactional
    public UpdatePostResponse updatePost(String postId, UpdatePostRequest updatePostRequest, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        String userId = userAuth.getUser().getId().toString();

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        String userID = post.getUser().getId().toString();

        if (userID.equals(userId)) {
            // change content and visibility
            post.setContent(updatePostRequest.content());
            post.setVisibility(updatePostRequest.visibility());

            // change media list
            for (UpdatePostMediaRequest updatePostMediaRequest : updatePostRequest.updatePostMediaRequests()) {
                PostMedia postMedia = new PostMedia(post, updatePostMediaRequest.type(), updatePostMediaRequest.objectKey(), updatePostMediaRequest.orderIndex());
                if (updatePostMediaRequest.operation() == MediaOperation.DELETE) {
                    post.getMediaList().remove(postMedia);
                    postMedia.setPost(null);
                }
                if (updatePostMediaRequest.operation() == MediaOperation.ADD) {
                    post.getMediaList().add(postMedia);
                }
            }
            postRepository.save(post);

            PostDTO postDTO = postMapper.toDTO(post);
            return new UpdatePostResponse(postDTO);
        } else throw new ApiException(ResponseCode.CANNOT_UPDATE_POST);
    }

    @Transactional
    public void deletePost(String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        String userId = userAuth.getUser().getId().toString();

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        String userID = post.getUser().getId().toString();

        if (userID.equals(userId)) {
            // dùng cách này vì xóa được cả PostMedia
            postRepository.delete(post);
            PostDTO postDTO = postMapper.toDTO(post);

            new DeletePostResponse(postDTO);
        } else throw new ApiException(ResponseCode.CANNOT_DELETE_POST);
    }

    public boolean canViewPost(User viewer, Post post) {
        if (post.getVisibility() == PostVisibility.PUBLIC) return true;

        if (post.getUser().getId().equals(viewer.getId())) return true;

        if (post.getVisibility() == PostVisibility.FRIENDS) {
            return friendshipService.isFriend(viewer.getId(), post.getUser().getId());
        }

        return false;
    }
}
