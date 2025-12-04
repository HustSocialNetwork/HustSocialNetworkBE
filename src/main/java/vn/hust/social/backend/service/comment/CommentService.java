package vn.hust.social.backend.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.CommentDTO;
import vn.hust.social.backend.dto.comment.create.CreateCommentMediaRequest;
import vn.hust.social.backend.dto.comment.create.CreateCommentRequest;
import vn.hust.social.backend.dto.comment.create.CreateCommentResponse;
import vn.hust.social.backend.dto.comment.get.GetCommentsResponse;
import vn.hust.social.backend.dto.comment.update.UpdateCommentMediaRequest;
import vn.hust.social.backend.dto.comment.update.UpdateCommentRequest;
import vn.hust.social.backend.dto.comment.update.UpdateCommentResponse;
import vn.hust.social.backend.entity.comment.Comment;
import vn.hust.social.backend.entity.comment.CommentMedia;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.CommentMapper;
import vn.hust.social.backend.repository.comment.CommentMediaRepository;
import vn.hust.social.backend.repository.comment.CommentRepository;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.service.post.PostService;
import vn.hust.social.backend.service.friendship.FriendshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final FriendshipService friendshipService;
    private final CommentRepository commentRepository;
    private final CommentMediaRepository commentMediaRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;

    @Transactional
    public GetCommentsResponse getComments(String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

        if (!canViewComments(userAuth.getUser(), post)) throw new ApiException(ResponseCode.CANNOT_VIEW_COMMENTS);

        List<Comment> comments = post.getComments();
        List<CommentDTO> commentDTOs = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDTO commentDTO = commentMapper.toDTO(comment);
            commentDTOs.add(commentDTO);
        }

        return new GetCommentsResponse(commentDTOs);
    }

    @Transactional
    public CreateCommentResponse createComment(CreateCommentRequest createCommentRequest, String email) {
        Post post = postRepository.findByPostId(UUID.fromString(createCommentRequest.postId())).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User commenter =  userAuth.getUser();

        if (!postService.canViewPost(commenter, post)) throw new ApiException(ResponseCode.CANNOT_VIEW_POST);

        Comment comment = new Comment(commenter, post, createCommentRequest.content());
        for (CreateCommentMediaRequest createCommentMediaRequest : createCommentRequest.createCommentMediaRequest()) {
            comment.getMediaList().add(new CommentMedia(
                    comment,
                    createCommentMediaRequest.type(),
                    createCommentMediaRequest.objectKey(),
                    createCommentMediaRequest.orderIndex()
            ));
        }
        comment.setPost(post);
        post.getComments().add(comment);
        Comment savedComment = commentRepository.saveAndFlush(comment);
        CommentDTO commentDTO = commentMapper.toDTO(savedComment);

        return new CreateCommentResponse(commentDTO);
    }

    @Transactional
    public UpdateCommentResponse updateComment(UpdateCommentRequest updateCommentRequest, String commentId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        String userId = userAuth.getUser().getId().toString();
        UUID commentID = UUID.fromString(commentId);
        Comment comment = commentRepository.findCommentById(commentID);
        String userID = comment.getUser().getId().toString();

        if (userId.equals(userID)) {
            // change content
            comment.setContent(updateCommentRequest.content());

            // change media list
            for (UpdateCommentMediaRequest updateCommentMediaRequest : updateCommentRequest.updateCommentMediaRequests()) {

                if (updateCommentMediaRequest.operation() == MediaOperation.DELETE) {
                    CommentMedia commentMedia = commentMediaRepository.getCommentMediaByObjectKey(updateCommentMediaRequest.objectKey());
                    comment.getMediaList().remove(commentMedia);
                    commentMedia.setComment(null);
                }
                if (updateCommentMediaRequest.operation() == MediaOperation.ADD) {
                    CommentMedia commentMedia = new CommentMedia(comment, updateCommentMediaRequest.type(), updateCommentMediaRequest.objectKey(), updateCommentMediaRequest.orderIndex());
                    comment.getMediaList().add(commentMedia);
                }
            }
            Comment savedComment = commentRepository.saveAndFlush(comment);
            CommentDTO commentDTO = commentMapper.toDTO(savedComment);

            return new UpdateCommentResponse(commentDTO);
        } else throw new ApiException(ResponseCode.CANNOT_UPDATE_COMMENT);
    }

    @Transactional
    public void deleteComment(String commentId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        String userId = userAuth.getUser().getId().toString();
        UUID commentID = UUID.fromString(commentId);
        Comment comment = commentRepository.getCommentById(commentID);
        String userID = comment.getUser().getId().toString();

        if (userId.equals(userID)) {
            commentRepository.delete(comment);
        } else throw new ApiException(ResponseCode.CANNOT_DELETE_COMMENT);
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
