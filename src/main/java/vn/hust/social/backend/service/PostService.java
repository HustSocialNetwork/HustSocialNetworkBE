package vn.hust.social.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.dto.post.*;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.post.PostMedia;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.repository.PostRepository;
import vn.hust.social.backend.repository.UserAuthRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Service
public class PostService {

    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final MediaService mediaService;

    @Transactional
    public GetPostResponse getPost (String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String userId = userAuth.getUser().getId().toString();

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new RuntimeException("Post not found"));
        String userID = post.getUser().getId().toString();
        List<GetPostMediaResponse> postMedias = new ArrayList<>();

        if (userID.equals(userId)) {
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

            return new GetPostResponse(post, postMedias);

        } else throw new RuntimeException("User not authorized");

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

        return new CreatePostResponse(post);
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
                if (updatePostMediaRequest.operation().equalsIgnoreCase("delete")) {
                    post.getMediaList().remove(postMedia);
                    postRepository.save(post);
                }
                if (updatePostMediaRequest.operation().equalsIgnoreCase("update")) {
                    post.getMediaList().add(postMedia);
                    postRepository.save(post);
                }
            }

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

            return new UpdatePostResponse(post, postMedias);
        } else throw new RuntimeException("User not authorized");
    }

    @Transactional
    public DeletePostResponse deletePost(String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String userId = userAuth.getUser().getId().toString();

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new RuntimeException("Post not found"));
        String userID = post.getUser().getId().toString();

        if (userID.equals(userId)) {
            // dùng cách này vì xóa được cả PostMedia
            postRepository.delete(post);
            return new DeletePostResponse(post);
        } else throw new RuntimeException("User not authorized");

    }
}
