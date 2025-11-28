package vn.hust.social.backend.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.dto.post.*;
import vn.hust.social.backend.dto.post.create.CreatePostMediaRequest;
import vn.hust.social.backend.dto.post.create.CreatePostRequest;
import vn.hust.social.backend.dto.post.create.CreatePostResponse;
import vn.hust.social.backend.dto.post.delete.DeletePostResponse;
import vn.hust.social.backend.dto.post.get.GetPostMediaResponse;
import vn.hust.social.backend.dto.post.get.GetPostResponse;
import vn.hust.social.backend.dto.post.update.UpdatePostMediaRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostMediaResponse;
import vn.hust.social.backend.dto.post.update.UpdatePostRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostResponse;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.post.PostMedia;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.mapper.PostMapper;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.user.UserAuthRepository;
import vn.hust.social.backend.service.media.MediaService;
import vn.hust.social.backend.service.user.FriendshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Service
public class PostService {

    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final MediaService mediaService;
    private final FriendshipService friendshipService;
    private final PostMapper postMapper;

    @Transactional
    public GetPostResponse getPost (String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new RuntimeException("Post not found"));
        List<GetPostMediaResponse> postMedias = new ArrayList<>();

        if (!canViewPost(userAuth.getUser(), post)) throw new RuntimeException("User not permitted to view post");

        List<PostMedia> mediaList = post.getMediaList();
        List<String> objectKeys = mediaList.stream().map(PostMedia::getObjectKey).toList();
        List<String> presignedUrlsForDownloading = mediaService.getPresignedObjectUrlsForDownloading(objectKeys, "post");

        for (int i=0; i<presignedUrlsForDownloading.size(); i++) {
            String objectKey = mediaList.get(i).getObjectKey();
            String presignedUrlForDownloading = presignedUrlsForDownloading.get(i);
            String type = mediaList.get(i).getType().toString();
            String orderIndex = String.valueOf(mediaList.get(i).getOrderIndex());
            postMedias.add(new GetPostMediaResponse(objectKey, presignedUrlForDownloading, type, orderIndex));
        }
        PostDTO postDTO = postMapper.toDTO(post);

        return new GetPostResponse(postDTO, postMedias);
    }

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest createPostRequest, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        User user = userAuth.getUser();
        Post post = new Post(user, createPostRequest.content(), createPostRequest.visibility());
        for (CreatePostMediaRequest postMedia : createPostRequest.createPostMediaRequests()) {
            PostMedia postmedia = new PostMedia(post, postMedia.type(), postMedia.objectKey(), postMedia.orderIndex());
            post.getMediaList().add(postmedia);
        }
        postRepository.save(post);
        PostDTO postDTO = postMapper.toDTO(post);

        return new CreatePostResponse(postDTO);
    }

    @Transactional
    public UpdatePostResponse updatePost(String postId, UpdatePostRequest updatePostRequest, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String userId = userAuth.getUser().getId().toString();

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new RuntimeException("Post not found"));
        String userID = post.getUser().getId().toString();
        List<UpdatePostMediaResponse> postMedias = new ArrayList<>();

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

            List<PostMedia> mediaList = post.getMediaList();
            List<String> objectKeys = mediaList.stream().map(PostMedia::getObjectKey).toList();
            List<String> presignedUrlsForDownloading = mediaService.getPresignedObjectUrlsForDownloading(objectKeys, "post");

            for (int i=0; i<presignedUrlsForDownloading.size(); i++) {
                String objectKey = mediaList.get(i).getObjectKey();
                String presignedUrlForDownloading = presignedUrlsForDownloading.get(i);
                String type = mediaList.get(i).getType().toString();
                String orderIndex = String.valueOf(mediaList.get(i).getOrderIndex());
                postMedias.add(new UpdatePostMediaResponse(objectKey, presignedUrlForDownloading, type, orderIndex));
            }

            PostDTO postDTO = postMapper.toDTO(post);
            return new UpdatePostResponse(postDTO, postMedias);
        } else throw new RuntimeException("User not authorized");
    }

    @Transactional
    public void deletePost(String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String userId = userAuth.getUser().getId().toString();

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new RuntimeException("Post not found"));
        String userID = post.getUser().getId().toString();

        if (userID.equals(userId)) {
            // dùng cách này vì xóa được cả PostMedia
            postRepository.delete(post);
            PostDTO postDTO = postMapper.toDTO(post);

            new DeletePostResponse(postDTO);
        } else throw new RuntimeException("User not authorized");
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
