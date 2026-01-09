package vn.hust.social.backend.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.CommentDTO;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.dto.media.CreateMediaRequest;
import vn.hust.social.backend.dto.media.UpdateMediaRequest;
import vn.hust.social.backend.dto.comment.create.CreateCommentRequest;
import vn.hust.social.backend.dto.comment.create.CreateCommentResponse;
import vn.hust.social.backend.dto.comment.get.GetCommentsResponse;
import vn.hust.social.backend.dto.comment.update.UpdateCommentRequest;
import vn.hust.social.backend.dto.comment.update.UpdateCommentResponse;
import vn.hust.social.backend.entity.comment.Comment;
import vn.hust.social.backend.entity.media.Media;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.CommentMapper;
import vn.hust.social.backend.mapper.MediaMapper;
import vn.hust.social.backend.repository.block.BlockRepository;
import vn.hust.social.backend.repository.media.MediaRepository;
import vn.hust.social.backend.repository.comment.CommentRepository;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.like.LikeRepository;
import vn.hust.social.backend.service.notification.NotificationService;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.enums.notification.NotificationType;
import vn.hust.social.backend.service.post.PostPermissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final CommentRepository commentRepository;
    private final MediaRepository mediaRepository;
    private final PostPermissionService postPermissionService;
    private final BlockRepository blockRepository;
    private final CommentMapper commentMapper;
    private final MediaMapper mediaMapper;
    private final NotificationService notificationService;
    private final LikeRepository likeRepository;

    @Transactional
    public GetCommentsResponse getComments(String postId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID postID = UUID.fromString(postId);
        Post post = postRepository.findByPostId(postID)
                .orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

        if (!postPermissionService.canViewPost(userAuth.getUser(), post))
            throw new ApiException(ResponseCode.CANNOT_VIEW_COMMENTS);

        List<Comment> comments = commentRepository.findByPostId(post.getPostId());
        List<CommentDTO> commentDTOs = new ArrayList<>();

        for (Comment comment : comments) {
            if (canViewComment(userAuth.getUser().getId(), comment)) {
                List<MediaDTO> medias = mediaRepository
                        .findByTargetIdAndTargetType(comment.getId(), MediaTargetType.COMMENT)
                        .stream()
                        .map(mediaMapper::toDTO)
                        .toList();
                boolean likedByViewer = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                        userAuth.getUser().getId(),
                        comment.getId(),
                        TargetType.COMMENT);
                CommentDTO commentDTO = commentMapper.toDTO(comment, medias, likedByViewer);
                commentDTOs.add(commentDTO);
            }
        }

        return new GetCommentsResponse(commentDTOs);
    }

    @Transactional
    public CreateCommentResponse createComment(
            CreateCommentRequest request,
            String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User commenter = userAuth.getUser();

        Post post = postRepository.findByPostId(UUID.fromString(request.postId()))
                .orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));

        if (!postPermissionService.canViewPost(commenter, post)) {
            throw new ApiException(ResponseCode.CANNOT_VIEW_POST);
        }

        Comment parent = null;
        if (request.parentCommentId() != null) {
            parent = commentRepository.findById(UUID.fromString(request.parentCommentId()))
                    .orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));

            if (!parent.getPost().getPostId().equals(post.getPostId())) {
                throw new ApiException(ResponseCode.INVALID_PARENT_COMMENT);
            }
        }

        Comment comment = new Comment(
                commenter,
                post,
                parent,
                request.content());
        commentRepository.save(comment);

        List<CreateMediaRequest> mediaRequests = request.createCommentMediaRequest() != null
                ? request.createCommentMediaRequest()
                : List.of();

        for (CreateMediaRequest mediaRequest : mediaRequests) {
            Media media = new Media(
                    comment.getId(),
                    MediaTargetType.COMMENT,
                    mediaRequest.type(),
                    mediaRequest.objectKey(),
                    mediaRequest.orderIndex());
            mediaRepository.save(media);
        }

        post.setCommentsCount(post.getCommentsCount() + 1);

        if (parent != null) {
            parent.setRepliesCount(parent.getRepliesCount() + 1);
        }

        List<MediaDTO> medias = mediaRepository.findByTargetIdAndTargetType(comment.getId(), MediaTargetType.COMMENT)
                .stream()
                .map(mediaMapper::toDTO)
                .toList();

        if (parent == null) {
            notificationService.sendNotification(post.getUser(), commenter, NotificationType.COMMENT_POST,
                    post.getPostId());
        } else {
            notificationService.sendNotification(parent.getUser(), commenter, NotificationType.REPLY_COMMENT,
                    parent.getId());
        }

        return new CreateCommentResponse(commentMapper.toDTO(comment, medias, false));
    }

    @Transactional
    public UpdateCommentResponse updateComment(UpdateCommentRequest updateCommentRequest, String commentId,
            String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        String userId = userAuth.getUser().getId().toString();
        UUID commentID = UUID.fromString(commentId);
        Comment comment = commentRepository.findCommentById(commentID);
        String userID = comment.getUser().getId().toString();

        if (userId.equals(userID)) {
            comment.setContent(updateCommentRequest.content());

            for (UpdateMediaRequest updateCommentMediaRequest : updateCommentRequest.updateCommentMediaRequests()) {

                if (updateCommentMediaRequest.operation() == MediaOperation.DELETE) {
                    mediaRepository.deleteByObjectKeyAndTargetType(updateCommentMediaRequest.objectKey(),
                            MediaTargetType.COMMENT);
                }
                if (updateCommentMediaRequest.operation() == MediaOperation.ADD) {
                    Media media = new Media(
                            comment.getId(),
                            MediaTargetType.COMMENT,
                            updateCommentMediaRequest.type(),
                            updateCommentMediaRequest.objectKey(),
                            updateCommentMediaRequest.orderIndex());
                    mediaRepository.save(media);
                }
            }
            Comment savedComment = commentRepository.saveAndFlush(comment);
            List<MediaDTO> medias = mediaRepository
                    .findByTargetIdAndTargetType(savedComment.getId(), MediaTargetType.COMMENT)
                    .stream()
                    .map(mediaMapper::toDTO)
                    .toList();
            boolean likedByViewer = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                    userAuth.getUser().getId(),
                    savedComment.getId(),
                    TargetType.COMMENT);
            CommentDTO commentDTO = commentMapper.toDTO(savedComment, medias, likedByViewer);

            return new UpdateCommentResponse(commentDTO);
        } else
            throw new ApiException(ResponseCode.CANNOT_UPDATE_COMMENT);
    }

    @Transactional
    public void deleteComment(String commentId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        String userId = userAuth.getUser().getId().toString();
        UUID commentID = UUID.fromString(commentId);
        Comment comment = commentRepository.getCommentById(commentID);
        String userID = comment.getUser().getId().toString();

        if (userId.equals(userID)) {
            commentRepository.delete(comment);
        } else
            throw new ApiException(ResponseCode.CANNOT_DELETE_COMMENT);
    }

    public boolean canViewComment(UUID viewerId, Comment comment) {
        if (blockRepository.existsByBlockerIdAndBlockedId(viewerId, comment.getUser().getId())) {
            return false;
        }

        return !blockRepository.existsByBlockerIdAndBlockedId(comment.getUser().getId(), viewerId);
    }
}
