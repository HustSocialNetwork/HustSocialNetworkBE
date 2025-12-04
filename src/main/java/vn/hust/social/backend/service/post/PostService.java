package vn.hust.social.backend.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.PostDTO;
import vn.hust.social.backend.dto.post.create.CreatePostMediaRequest;
import vn.hust.social.backend.dto.post.create.CreatePostRequest;
import vn.hust.social.backend.dto.post.create.CreatePostResponse;
import vn.hust.social.backend.dto.post.delete.DeletePostResponse;
import vn.hust.social.backend.dto.post.get.GetPostResponse;
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

import java.util.UUID;

@RequiredArgsConstructor @Service
public class PostService {

    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final FriendshipService friendshipService;
    private final PostMapper postMapper;

    @Transactional
    public GetPostResponse getPost (String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

        if (!canViewPost(userAuth.getUser(), post)) throw new ApiException(ResponseCode.CANNOT_VIEW_POST);

        PostDTO postDTO = postMapper.toDTO(post);

        return new GetPostResponse(postDTO);
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
            return friendshipService.isFriend(viewer, post.getUser());
        }

        return false;
    }
}
