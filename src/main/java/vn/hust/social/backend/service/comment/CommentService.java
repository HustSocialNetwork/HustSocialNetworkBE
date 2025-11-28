package vn.hust.social.backend.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.dto.comment.*;
import vn.hust.social.backend.dto.user.UserDto;
import vn.hust.social.backend.entity.comment.Comment;
import vn.hust.social.backend.entity.comment.CommentMedia;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.repository.comment.CommentRepository;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.user.UserAuthRepository;
import vn.hust.social.backend.service.media.MediaService;
import vn.hust.social.backend.service.post.PostService;
import vn.hust.social.backend.service.user.FriendshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final MediaService mediaService;
    private final FriendshipService friendshipService;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public GetCommentsResponse getComments(String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!canViewComments(userAuth.getUser(), post)) throw new RuntimeException("User not permitted to view comments");

        List<Comment> comments = post.getComments();
        List<GetCommentResponse> getCommentResponseList = new ArrayList<>();

        for (Comment comment : comments) {
            List<CommentMedia> mediaList = comment.getMediaList();
            List<String> objectKeys = mediaList.stream().map(CommentMedia::getObjectKey).toList();
            List<String> presignedUrlsForDownloading = mediaService.getPresignedObjectUrlsForDownloading(objectKeys, "comment");
            List<GetCommentMediaResponse> getCommentMediaResponseList = new ArrayList<>();
            for (int i = 0 ; i < presignedUrlsForDownloading.size(); i++){
                getCommentMediaResponseList.add(new GetCommentMediaResponse(
                        mediaList.get(i).getObjectKey(),
                        mediaList.get(i).getType().toString(),
                        mediaList.get(i).getOrderIndex().toString(),
                        presignedUrlsForDownloading.get(i)
                ));
            }
            getCommentResponseList.add(new GetCommentResponse(
                    comment.getId(),
                    new UserDto(
                            comment.getUser().getId(),
                            comment.getUser().getFirstName(),
                            comment.getUser().getLastName(),
                            comment.getUser().getDisplayName(),
                            comment.getUser().getCreatedAt()
                    ),
                    comment.getContent(),
                    comment.getLikesCount(),
                    getCommentMediaResponseList
            ));
        }

        return new GetCommentsResponse(getCommentResponseList);
    }

    @Transactional
    public void deleteComment(String commentId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
        String userId = userAuth.getUser().getId().toString();

        UUID commentID = UUID.fromString(commentId);
        Comment comment = commentRepository.getCommentById(commentID);
        String userID = comment.getUser().getId().toString();

        if (userId.equals(userID)) {
            commentRepository.delete(comment);
        } else throw new RuntimeException("User not authorized");
    }

    @Transactional
    public CreateCommentResponse postComment(CreateCommentRequest createCommentRequest, String email) {
        Post post = postRepository.findByPostId(UUID.fromString(createCommentRequest.postId())).orElseThrow(() -> new RuntimeException("Post not found"));
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
        User commenter =  userAuth.getUser();

        if (!postService.canViewPost(commenter, post)) throw new RuntimeException("User not permitted to view posts");

        Comment comment = new Comment(commenter, post, createCommentRequest.content());
        for (CreateCommentMediaRequest createCommentMediaRequest : createCommentRequest.createCommentMediaRequest()) {
            comment.getMediaList().add(new CommentMedia(
                    comment,
                    createCommentMediaRequest.type(),
                    createCommentMediaRequest.objectKey(),
                    createCommentMediaRequest.orderIndex()
            ));
        }
        post.getComments().add(comment);
        postRepository.save(post);

        return new CreateCommentResponse(comment);
    }

    @Transactional
    public UpdateCommentResponse updateComment(UpdateCommentRequest updateCommentRequest, String commentId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
        String userId = userAuth.getUser().getId().toString();

        UUID commentID = UUID.fromString(commentId);
        Comment comment = commentRepository.getCommentById(commentID);
        String userID = comment.getUser().getId().toString();

        if (userId.equals(userID)) {
            // change content
            comment.setContent(updateCommentRequest.content());

            // change media list
            for (UpdateCommentMediaRequest updateCommentMediaRequest : updateCommentRequest.updateCommentMediaRequests()) {
                CommentMedia commentMedia = new CommentMedia(comment, updateCommentMediaRequest.type(), updateCommentMediaRequest.objectKey(), updateCommentMediaRequest.orderIndex());
                if (updateCommentMediaRequest.operation() == MediaOperation.DELETE) {
                    comment.getMediaList().remove(commentMedia);
                    commentMedia.setComment(null);
                }
                if (updateCommentMediaRequest.operation() == MediaOperation.ADD) {
                    comment.getMediaList().add(commentMedia);
                }
            }
            commentRepository.save(comment);

            return new UpdateCommentResponse(comment);
        } else throw new RuntimeException("User not authorized");
    }

    private boolean canViewComments(User viewer, Post post) {
        if (post.getVisibility() == PostVisibility.PUBLIC) return true;

        if (post.getUser().getId().equals(viewer.getId())) return true;

        if (post.getVisibility() == PostVisibility.FRIENDS) {
            return friendshipService.isFriend(viewer, post.getUser());
        }

        return false;
    }
}
