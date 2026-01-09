package vn.hust.social.backend.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.common.response.WsResponse;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.dto.MessageDTO;
import vn.hust.social.backend.dto.chat.*;
import vn.hust.social.backend.dto.media.CreateMediaRequest;
import vn.hust.social.backend.entity.chat.Conversation;
import vn.hust.social.backend.entity.chat.ConversationMember;
import vn.hust.social.backend.entity.chat.Message;
import vn.hust.social.backend.entity.chat.MessageRead;
import vn.hust.social.backend.entity.enums.chat.ConversationType;
import vn.hust.social.backend.entity.enums.chat.MemberType;
import vn.hust.social.backend.entity.enums.chat.MessageType;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.media.Media;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.exception.WsException;
import vn.hust.social.backend.mapper.*;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.chat.ConversationMemberRepository;
import vn.hust.social.backend.repository.chat.ConversationRepository;
import vn.hust.social.backend.repository.chat.MessageReadRepository;
import vn.hust.social.backend.repository.chat.MessageRepository;
import vn.hust.social.backend.repository.media.MediaRepository;
import vn.hust.social.backend.repository.user.UserRepository;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {
        private final SimpMessagingTemplate messagingTemplate;
        private final UserRepository userRepository;
        private final UserAuthRepository userAuthRepository;
        private final ConversationMemberRepository conversationMemberRepository;
        private final ConversationRepository conversationRepository;
        private final MessageRepository messageRepository;
        private final MessageReadRepository messageReadRepository;
        private final MediaRepository mediaRepository;
        private final ConversationMapper conversationMapper;
        private final ConversationMemberMapper conversationMemberMapper;
        private final MediaMapper mediaMapper;
        private final MessageMapper messageMapper;
        private final UserMapper userMapper;

        @Transactional(readOnly = true)
        public List<GetConversationResponse> getConversationsOfUser(String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                List<ConversationMember> conversationMembers = conversationMemberRepository
                                .findByMember(user);

                return conversationMembers.stream()
                                .map(member -> convertToGetConversationResponse(member.getConversation()))
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<GetMessagesResponse> getMessages(UUID conversationId, int limit, Long after, Long before,
                        String email) {
                User user = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, user);

                Pageable pageable = PageRequest.of(0, limit);
                List<Message> messages;

                if (before != null) {
                        Instant beforeInstant = Instant.ofEpochMilli(before);
                        messages = messageRepository.findByConversationBeforeTimestamp(conversation, beforeInstant,
                                        pageable);
                } else if (after != null) {
                        Instant afterInstant = Instant.ofEpochMilli(after);
                        messages = messageRepository.findByConversationAfterTimestamp(conversation, afterInstant,
                                        pageable);
                } else {
                        messages = messageRepository.findByConversationOrderByCreatedAtDesc(conversation, pageable);
                }

                return messages.stream()
                                .map(msg -> new GetMessagesResponse(
                                                msg.getId(),
                                                msg.getSender().getId(),
                                                msg.getContent(),
                                                msg.getCreatedAt(),
                                                Collections.emptyList()))
                                .toList();
        }

        @Transactional(readOnly = true)
        public void broadcastTyping(WsTypingRequest request, Principal principal) {
                String email = principal.getName();
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new WsException(ResponseCode.USER_NOT_FOUND));
                User sender = userAuth.getUser();

                Conversation conversation = conversationRepository.findById(request.conversationId())
                                .orElseThrow(() -> new WsException(ResponseCode.CONVERSATION_NOT_FOUND));

                boolean isMember = conversationMemberRepository.findByConversation(conversation)
                                .stream()
                                .anyMatch(m -> m.getMember().getId().equals(sender.getId()));

                if (!isMember) {
                        throw new WsException(ResponseCode.CANNOT_ACCESS_MESSAGES);
                }

                WsTypingResponse response = new WsTypingResponse(
                                conversation.getId(),
                                userMapper.toDTO(sender),
                                request.isTyping());
                WsResponse<WsTypingResponse> wsResponse = WsResponse.success(response);

                if (conversation.getType() == ConversationType.GROUP) {
                        messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), wsResponse);
                } else {
                        User recipient = conversationMemberRepository.findByConversation(conversation).stream()
                                        .filter(m -> !m.getMember().getId().equals(sender.getId()))
                                        .findFirst()
                                        .map(ConversationMember::getMember)
                                        .orElseThrow(() -> new WsException(ResponseCode.RECIPIENT_NOT_FOUND));

                        UserAuth recipientAuth = userAuthRepository.findByUserId(recipient.getId()).stream()
                                        .findFirst()
                                        .orElseThrow(() -> new WsException(ResponseCode.RECIPIENT_NOT_FOUND));

                        messagingTemplate.convertAndSendToUser(
                                        recipientAuth.getEmail(),
                                        "/queue/typing",
                                        wsResponse);
                }
        }

        @Transactional
        public void markMessageAsRead(WsReadRequest request, Principal principal) {
                String email = principal.getName();
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new WsException(ResponseCode.USER_NOT_FOUND));
                User reader = userAuth.getUser();

                Conversation conversation = conversationRepository.findById(request.conversationId())
                                .orElseThrow(() -> new WsException(ResponseCode.CONVERSATION_NOT_FOUND));

                boolean isMember = conversationMemberRepository.findByConversation(conversation)
                                .stream()
                                .anyMatch(m -> m.getMember().getId().equals(reader.getId()));

                if (!isMember) {
                        throw new WsException(ResponseCode.CANNOT_ACCESS_MESSAGES);
                }

                Message message = messageRepository.findById(request.messageId())
                                .orElseThrow(() -> new WsException(ResponseCode.MESSAGE_NOT_FOUND));

                if (!message.getConversation().getId().equals(conversation.getId())) {
                        throw new WsException(ResponseCode.MESSAGE_NOT_FOUND);
                }

                // Persist read status if not exists
                if (messageReadRepository.findByMessageAndReader(message, reader).isEmpty()) {
                        MessageRead messageRead = new MessageRead(message, reader);
                        messageReadRepository.save(messageRead);
                }

                WsReadResponse response = new WsReadResponse(
                                conversation.getId(),
                                message.getId(),
                                userMapper.toDTO(reader),
                                Instant.now());
                WsResponse<WsReadResponse> wsResponse = WsResponse.success(response);

                if (conversation.getType() == ConversationType.GROUP) {
                        messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), wsResponse);
                } else {
                        User recipient = conversationMemberRepository.findByConversation(conversation).stream()
                                        .filter(m -> !m.getMember().getId().equals(reader.getId()))
                                        .findFirst()
                                        .map(ConversationMember::getMember)
                                        .orElseThrow(() -> new WsException(ResponseCode.RECIPIENT_NOT_FOUND));

                        UserAuth recipientAuth = userAuthRepository.findByUserId(recipient.getId()).stream()
                                        .findFirst()
                                        .orElseThrow(() -> new WsException(ResponseCode.RECIPIENT_NOT_FOUND));

                        messagingTemplate.convertAndSendToUser(
                                        recipientAuth.getEmail(),
                                        "/queue/read",
                                        wsResponse);
                }
        }

        @Transactional
        public SendMessageWithMediaResponse sendMessage(UUID conversationId, SendMessageRequest request, String email) {
                User sender = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, sender);

                if ((request.content() == null || request.content().trim().isEmpty())
                                && (request.medias() == null || request.medias().isEmpty())) {
                        throw new ApiException(ResponseCode.VALIDATION_ERROR);
                }

                Message message = new Message(conversation, sender, request.content(), MessageType.USER);
                Message savedMessage = messageRepository.save(message);

                List<MediaDTO> mediaDTOs = new ArrayList<>();

                if (request.medias() != null && !request.medias().isEmpty()) {
                        for (int i = 0; i < request.medias().size(); i++) {
                                CreateMediaRequest mediaRequest = request.medias().get(i);

                                Media media = new Media(
                                                savedMessage.getId(),
                                                MediaTargetType.MESSAGE,
                                                mediaRequest.type(),
                                                mediaRequest.objectKey(),
                                                i);
                                Media savedMedia = mediaRepository.save(media);
                                mediaDTOs.add(mediaMapper.toDTO(savedMedia));
                        }
                }

                MessageDTO messageDTO = messageMapper.toDTO(savedMessage);

                WsMessageResponse wsMessageResponse = new WsMessageResponse(
                                messageDTO,
                                mediaDTOs);
                WsResponse<WsMessageResponse> wsResponse = WsResponse.success(wsMessageResponse);

                if (conversation.getType() == ConversationType.GROUP) {
                        messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), wsResponse);
                } else {
                        User recipient = conversationMemberRepository.findByConversation(conversation).stream()
                                        .filter(m -> !m.getMember().getId().equals(sender.getId()))
                                        .findFirst()
                                        .map(ConversationMember::getMember)
                                        .orElseThrow(() -> new ApiException(ResponseCode.RECIPIENT_NOT_FOUND));

                        UserAuth recipientAuth = userAuthRepository.findByUserId(recipient.getId()).stream()
                                        .findFirst()
                                        .orElseThrow(() -> new ApiException(ResponseCode.RECIPIENT_NOT_FOUND));

                        messagingTemplate.convertAndSendToUser(
                                        recipientAuth.getEmail(),
                                        "/queue/messages",
                                        wsResponse);

                        // Send ack to sender
                        messagingTemplate.convertAndSendToUser(
                                        email,
                                        "/queue/messages",
                                        wsResponse);
                }

                return new SendMessageWithMediaResponse(
                                messageDTO,
                                mediaDTOs);
        }

        @Transactional
        public CreateConversationResponse createConversation(CreateConversationRequest request, String email) {
                User creator = getUserFromEmail(email);
                List<User> participants = new ArrayList<>();
                if (request.participantIds() != null && !request.participantIds().isEmpty()) {
                        participants = userRepository.findAllById(request.participantIds());
                        if (participants.size() != request.participantIds().size()) {
                                throw new ApiException(ResponseCode.USER_NOT_FOUND);
                        }
                }

                Conversation conversation = new Conversation(
                                request.type(),
                                request.title(),
                                creator);
                conversation = conversationRepository.save(conversation);

                List<ConversationMember> members = new ArrayList<>();
                members.add(new ConversationMember(conversation, creator, MemberType.ADMIN));

                for (User participant : participants) {
                        if (!participant.getId().equals(creator.getId())) {
                                members.add(new ConversationMember(conversation, participant, MemberType.MEMBER));
                        }
                }

                members = conversationMemberRepository.saveAll(members);

                return new CreateConversationResponse(
                                conversationMapper.toDTO(conversation),
                                members.stream().map(conversationMemberMapper::toDTO).toList());
        }

        @Transactional
        public GetConversationResponse addMembers(UUID conversationId, AddMemberRequest request, String email) {
                User actor = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, actor);

                if (conversation.getType() != ConversationType.GROUP) {
                        throw new ApiException(ResponseCode.INVALID_CONVERSATION_TYPE);
                }

                List<User> usersToAdd = userRepository.findAllById(request.userIds());
                List<ConversationMember> currMembers = conversationMemberRepository.findByConversation(conversation);
                List<ConversationMember> newMembers = new ArrayList<>();

                for (User user : usersToAdd) {
                        boolean exists = currMembers.stream().anyMatch(m -> m.getMember().getId().equals(user.getId()));
                        if (!exists) {
                                newMembers.add(new ConversationMember(conversation, user, MemberType.MEMBER));
                                createSystemMessage(conversation, actor,
                                                actor.getFullName() + " added " + user.getFullName() + " to the group");
                        }
                }

                if (!newMembers.isEmpty()) {
                        conversationMemberRepository.saveAll(newMembers);
                }

                return convertToGetConversationResponse(conversation);
        }

        @Transactional
        public GetConversationResponse removeMember(UUID conversationId, UUID memberId, String email) {
                User actor = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, actor);

                if (conversation.getType() != ConversationType.GROUP) {
                        throw new ApiException(ResponseCode.INVALID_CONVERSATION_TYPE);
                }

                ConversationMember memberToRemove = conversationMemberRepository
                                .findByConversationAndMemberId(conversation, memberId)
                                .orElseThrow(() -> new ApiException(ResponseCode.MEMBER_NOT_FOUND));

                ConversationMember actorMember = conversationMemberRepository
                                .findByConversationAndMemberId(conversation, actor.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.MEMBER_NOT_FOUND));

                if (!actorMember.getMember().getId().equals(memberToRemove.getMember().getId())
                                && actorMember.getRole() != MemberType.ADMIN) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                if (memberToRemove.getRole() == MemberType.ADMIN
                                && conversationMemberRepository.findByConversation(conversation).size() > 1) {
                        long adminCount = conversationMemberRepository.findByConversation(conversation).stream()
                                        .filter(m -> m.getRole() == MemberType.ADMIN).count();
                        if (adminCount == 1) {
                                throw new ApiException(ResponseCode.LAST_ADMIN_CANNOT_LEAVE);
                        }
                }

                conversationMemberRepository.delete(memberToRemove);

                String messageContent = actorMember.getMember().getId().equals(memberToRemove.getMember().getId())
                                ? actor.getFullName() + " left the group"
                                : actor.getFullName() + " removed " + memberToRemove.getMember().getFullName()
                                                + " from the group";

                createSystemMessage(conversation, actor, messageContent);

                if (conversationMemberRepository.findByConversation(conversation).isEmpty()) {
                        deleteConversationLogic(conversation);
                        return null;
                }

                return convertToGetConversationResponse(conversation);
        }

        @Transactional
        public GetConversationResponse updateConversation(UUID conversationId, UpdateConversationRequest request,
                        String email) {
                User actor = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, actor);

                if (conversation.getType() != ConversationType.GROUP) {
                        throw new ApiException(ResponseCode.INVALID_CONVERSATION_TYPE);
                }

                if (request.name() != null && !request.name().isEmpty()
                                && !request.name().equals(conversation.getTitle())) {
                        conversation.setTitle(request.name());
                        conversationRepository.save(conversation);
                        createSystemMessage(conversation, actor,
                                        actor.getFullName() + " changed the group name to " + request.name());
                }

                if (request.image() != null) {
                        Media media = new Media(
                                        conversation.getId(),
                                        MediaTargetType.CONVERSATION,
                                        request.image().type(),
                                        request.image().objectKey(),
                                        0);
                        mediaRepository.save(media);
                        createSystemMessage(conversation, actor, actor.getFullName() + " changed the group photo");
                }

                return convertToGetConversationResponse(conversation);
        }

        private void createSystemMessage(Conversation conversation, User actor, String content) {
                Message message = new Message(conversation, actor, content, MessageType.SYSTEM);
                message = messageRepository.save(message);

                MessageDTO messageDTO = messageMapper.toDTO(message);
                WsMessageResponse wsMessageResponse = new WsMessageResponse(messageDTO, Collections.emptyList());
                WsResponse<WsMessageResponse> wsResponse = WsResponse.success(wsMessageResponse);

                messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), wsResponse);
        }

        private User getUserFromEmail(String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                return userAuth.getUser();
        }

        private Conversation validateAndGetConversation(UUID conversationId, User user) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CONVERSATION_NOT_FOUND));

                boolean isMember = conversationMemberRepository.findByConversation(conversation)
                                .stream()
                                .anyMatch(member -> member.getMember().getId().equals(user.getId()));

                if (!isMember) {
                        throw new ApiException(ResponseCode.CANNOT_ACCESS_MESSAGES);
                }

                return conversation;
        }

        private GetConversationResponse convertToGetConversationResponse(Conversation conversation) {
                List<ConversationMember> members = conversationMemberRepository
                                .findByConversation(conversation);
                var memberDTOs = members.stream()
                                .map(conversationMemberMapper::toDTO)
                                .toList();

                MessageDTO lastMessageDTO = null;
                List<MediaDTO> lastMessageMedias = new ArrayList<>();

                var lastMessageOpt = messageRepository.findFirstByConversationOrderByCreatedAtDesc(conversation);
                if (lastMessageOpt.isPresent()) {
                        Message lastMessage = lastMessageOpt.get();
                        lastMessageDTO = messageMapper.toDTO(lastMessage);
                        lastMessageMedias = mediaRepository
                                        .findByTargetIdAndTargetType(lastMessage.getId(), MediaTargetType.MESSAGE)
                                        .stream()
                                        .map(mediaMapper::toDTO)
                                        .toList();

                        if ((lastMessageDTO.content() == null || lastMessageDTO.content().isEmpty())
                                        && !lastMessageMedias.isEmpty()) {
                                String content = lastMessage.getSender().getFullName() + " sent "
                                                + lastMessageMedias.size() + " photos";
                                lastMessageDTO = new MessageDTO(
                                                lastMessageDTO.id(),
                                                content,
                                                lastMessageDTO.type(),
                                                lastMessageDTO.sender(),
                                                lastMessageDTO.conversation(),
                                                lastMessageDTO.createdAt(),
                                                lastMessageDTO.updatedAt());
                        }
                }

                MediaDTO conversationImage = mediaRepository
                                .findByTargetIdAndTargetType(conversation.getId(), MediaTargetType.CONVERSATION)
                                .stream()
                                .findFirst()
                                .map(mediaMapper::toDTO)
                                .orElse(null);

                return new GetConversationResponse(
                                conversationMapper.toDTO(conversation),
                                memberDTOs,
                                lastMessageDTO,
                                lastMessageMedias,
                                conversationImage);
        }

        @Transactional
        public void promoteMemberToAdmin(UUID conversationId, UUID memberId, String email) {
                User actor = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, actor);

                if (conversation.getType() != ConversationType.GROUP) {
                        throw new ApiException(ResponseCode.INVALID_CONVERSATION_TYPE);
                }

                ConversationMember actorMember = conversationMemberRepository
                                .findByConversationAndMemberId(conversation, actor.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.MEMBER_NOT_FOUND));

                if (actorMember.getRole() != MemberType.ADMIN) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                ConversationMember memberToPromote = conversationMemberRepository
                                .findByConversationAndMemberId(conversation, memberId)
                                .orElseThrow(() -> new ApiException(ResponseCode.MEMBER_NOT_FOUND));

                if (memberToPromote.getRole() == MemberType.ADMIN) {
                        return;
                }

                memberToPromote.setRole(MemberType.ADMIN);
                conversationMemberRepository.save(memberToPromote);

                createSystemMessage(conversation, actor,
                                actor.getFullName() + " promoted " + memberToPromote.getMember().getFullName()
                                                + " to admin");
        }

        @Transactional
        public void deleteConversation(UUID conversationId, String email) {
                User actor = getUserFromEmail(email);
                Conversation conversation = validateAndGetConversation(conversationId, actor);

                if (conversation.getType() == ConversationType.PRIVATE) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                ConversationMember actorMember = conversationMemberRepository
                                .findByConversationAndMemberId(conversation, actor.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.MEMBER_NOT_FOUND));

                if (actorMember.getRole() != MemberType.ADMIN) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                deleteConversationLogic(conversation);
        }

        private void deleteConversationLogic(Conversation conversation) {
                messageRepository.deleteByConversation(conversation);
                conversationMemberRepository.deleteByConversation(conversation);
                conversationRepository.delete(conversation);
        }
}
