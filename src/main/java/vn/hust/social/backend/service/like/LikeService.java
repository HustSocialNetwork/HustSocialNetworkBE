package vn.hust.social.backend.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.entity.comment.Comment;
import vn.hust.social.backend.entity.like.CommentLike;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.like.PostLike;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.comment.CommentRepository;
import vn.hust.social.backend.repository.like.CommentLikeRepository;
import vn.hust.social.backend.repository.like.PostLikeRepository;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.service.comment.CommentService;
import vn.hust.social.backend.service.post.PostService;

import java.util.UUID;

@RequiredArgsConstructor @Service
public class LikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserAuthRepository userAuthRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional
    public void likePost(UUID postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        UserAuth likerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User liker = likerUserAuth.getUser();
        if (postLikeRepository.existsByUserIdAndPostPostId(liker.getId(), postId)) throw new ApiException(ResponseCode.ALREADY_LIKED);
        if (postService.canViewPost(liker, post)) {
            // let front-end display the likes + 1 itself
            post.setLikesCount(post.getLikesCount() + 1);
            postLikeRepository.save(new PostLike(liker, post));
        } else throw new ApiException(ResponseCode.CANNOT_VIEW_POST);
    }

    @Transactional
    public void unlikePost(UUID postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        UserAuth likerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User liker = likerUserAuth.getUser();
        if (!postLikeRepository.existsByUserIdAndPostPostId(liker.getId(), postId)) throw new ApiException(ResponseCode.ALREADY_UNLIKED);
        if (postService.canViewPost(liker, post)) {
            // let front-end display the likes - 1 itself
            post.setLikesCount(post.getLikesCount() - 1);
            postLikeRepository.deletePostLikeByUserIdAndPostPostId(liker.getId(), postId);
        }
    }

    @Transactional
    public void likeComment(UUID commentId, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));
        UserAuth likerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User liker = likerUserAuth.getUser();
        if (commentLikeRepository.existsByUserIdAndCommentId(liker.getId(), commentId)) throw new ApiException(ResponseCode.ALREADY_LIKED);
        if (commentService.canViewComment(liker.getId(), comment)) {
            // let front-end display the likes + 1 itself
            comment.setLikesCount(comment.getLikesCount() + 1);
            commentLikeRepository.save(new CommentLike(liker, comment));
        } else throw new ApiException(ResponseCode.CANNOT_VIEW_COMMENT);
    }

    @Transactional
    public void unlikeComment(UUID commentId, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));
        UserAuth likerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User liker = likerUserAuth.getUser();
        if (!commentLikeRepository.existsByUserIdAndCommentId(liker.getId(), commentId)) throw new ApiException(ResponseCode.ALREADY_LIKED);
        if (commentService.canViewComment(liker.getId(), comment)) {
            // let front-end display the likes + 1 itself
            comment.setLikesCount(comment.getLikesCount() - 1);
            commentLikeRepository.deleteCommentLikeByUserIdAndCommentId(liker.getId(), commentId);
        } else throw new ApiException(ResponseCode.CANNOT_VIEW_COMMENT);
    }
}
