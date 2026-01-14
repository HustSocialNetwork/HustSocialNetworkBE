package vn.hust.social.backend.service.club;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.club.*;
import vn.hust.social.backend.entity.club.Club;
import vn.hust.social.backend.entity.club.ClubFollower;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.club.ClubFollowerRepository;
import vn.hust.social.backend.repository.club.ClubModeratorRepository;
import vn.hust.social.backend.repository.club.ClubRepository;
import vn.hust.social.backend.repository.user.UserRepository;
import vn.hust.social.backend.repository.event.EventRepository;
import vn.hust.social.backend.repository.event.EventParticipantRepository;
import vn.hust.social.backend.entity.event.Event;
import vn.hust.social.backend.entity.club.ClubModerator;
import vn.hust.social.backend.entity.enums.club.ClubRole;
import vn.hust.social.backend.entity.enums.club.ClubModeratorStatus;
import vn.hust.social.backend.entity.enums.club.ClubFollowerStatus;

import java.util.UUID;

import vn.hust.social.backend.mapper.ClubMapper;
import vn.hust.social.backend.service.notification.NotificationService;
import vn.hust.social.backend.entity.enums.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.hust.social.backend.dto.ClubDTO;

import vn.hust.social.backend.dto.ClubWithStatusDTO;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClubService {

        private final ClubRepository clubRepository;
        private final ClubFollowerRepository clubFollowerRepository;
        private final UserAuthRepository userAuthRepository;
        private final ClubModeratorRepository clubModeratorRepository;
        private final UserRepository userRepository;
        private final ClubMapper clubMapper;
        private final NotificationService notificationService;
        private final EventRepository eventRepository;
        private final EventParticipantRepository eventParticipantRepository;

        @Transactional
        public void followClub(UUID clubId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                if (clubFollowerRepository.existsByClubIdAndUserId(clubId, user.getId())
                                && clubFollowerRepository.findByClubIdAndUserId(clubId, user.getId()).get().getStatus()
                                                .equals(ClubFollowerStatus.ACTIVE)) {
                        throw new ApiException(ResponseCode.CLUB_ALREADY_FOLLOWED);
                }

                ClubFollower newFollower = new ClubFollower(club, user, ClubFollowerStatus.ACTIVE);
                clubFollowerRepository.save(newFollower);

                club.setFollowerCount(club.getFollowerCount() + 1);
                clubRepository.save(club);
        }

        @Transactional
        public void unfollowClub(UUID clubId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                ClubFollower follower = clubFollowerRepository.findByClubIdAndUserId(clubId, user.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOLLOWED));

                clubFollowerRepository.delete(follower);

                club.setFollowerCount(club.getFollowerCount() > 0 ? club.getFollowerCount() - 1 : 0);
                clubRepository.save(club);
        }

        @Transactional
        public CreateClubResponse createClub(CreateClubRequest request, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                if (clubRepository.existsByName(request.name())) {
                        throw new ApiException(ResponseCode.CLUB_NAME_ALREADY_EXISTS);
                }

                Club club = new Club(request.name(), request.description());
                club = clubRepository.save(club);

                ClubModerator moderator = new ClubModerator(club, user, ClubRole.CLUB_ADMIN,
                                ClubModeratorStatus.ACTIVE);
                clubModeratorRepository.save(moderator);

                return new CreateClubResponse(clubMapper.toClubDTO(club));
        }

        @Transactional
        public InviteFollowResponse inviteToFollow(UUID clubId, UUID studentId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                User student = userRepository.findById(studentId)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                if (clubFollowerRepository.existsByClubIdAndUserId(clubId, studentId)
                                && clubFollowerRepository.findByClubIdAndUserId(clubId, studentId).get().getStatus()
                                                .equals(ClubFollowerStatus.ACTIVE)) {
                        throw new ApiException(ResponseCode.CLUB_ALREADY_FOLLOWED_BY_RECEIVER);
                }

                ClubFollower newFollower = new ClubFollower(club, student, ClubFollowerStatus.INVITED);
                newFollower = clubFollowerRepository.save(newFollower);

                notificationService.sendNotification(student, user, NotificationType.INVITE_CLUB_FOLLOW, clubId);

                return new InviteFollowResponse(clubMapper.toClubFollowerDTO(newFollower));
        }

        @Transactional
        public UpdateClubResponse updateClub(UUID clubId, UpdateClubRequest request, String requesterEmail) {
                UserAuth requesterAuth = userAuthRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User requester = requesterAuth.getUser();

                boolean isSenderAdmin = clubModeratorRepository.findByClubIdAndUserId(clubId, requester.getId())
                                .map(m -> m.getRole() == ClubRole.CLUB_ADMIN
                                                && m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isSenderAdmin) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                if (request.name() != null && !request.name().isEmpty() && !request.name().equals(club.getName())) {
                        if (clubRepository.existsByName(request.name())) {
                                throw new ApiException(ResponseCode.CLUB_NAME_ALREADY_EXISTS);
                        }
                        club.setName(request.name());
                }

                if (request.description() != null) {
                        club.setDescription(request.description());
                }

                if (request.avatarKey() != null) {
                        club.setAvatarKey(request.avatarKey());
                }

                if (request.backgroundKey() != null) {
                        club.setBackgroundKey(request.backgroundKey());
                }

                club = clubRepository.save(club);

                return new UpdateClubResponse(clubMapper.toClubDTO(club));
        }

        @Transactional
        public InviteManageResponse inviteToManage(UUID clubId, UUID studentId, ClubRole role, String requesterEmail) {
                UserAuth requesterAuth = userAuthRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User requester = requesterAuth.getUser();

                boolean isSenderAdmin = clubModeratorRepository.findByClubIdAndUserId(clubId, requester.getId())
                                .map(m -> m.getRole() == ClubRole.CLUB_ADMIN
                                                && m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isSenderAdmin) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                User student = userRepository.findById(studentId)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                Optional<ClubModerator> existingModerator = clubModeratorRepository.findByClubIdAndUserId(clubId,
                                studentId);
                ClubModerator moderator;
                if (existingModerator.isPresent()) {
                        moderator = existingModerator.get();
                        if (moderator.getStatus() == ClubModeratorStatus.ACTIVE) {
                                throw new ApiException(ResponseCode.USER_ALREADY_MODERATOR);
                        } else if (moderator.getStatus() == ClubModeratorStatus.INVITED) {
                                throw new ApiException(ResponseCode.USER_HAS_ALREADY_BEEN_INVITED);
                        }
                        // For REJECTED, PENDING_APPLICATION, REVOKED, we can re-invite
                        moderator.setStatus(ClubModeratorStatus.INVITED);
                        moderator.setRole(role); // Update role if changed
                } else {
                        moderator = new ClubModerator(club, student, role, ClubModeratorStatus.INVITED);
                }

                moderator = clubModeratorRepository.save(moderator);

                notificationService.sendNotification(student, requester, NotificationType.INVITE_CLUB_MANAGE, clubId);

                return new InviteManageResponse(clubMapper.toClubModeratorDTO(moderator));
        }

        @Transactional
        public ApplyManageResponse applyToManage(UUID clubId, String message, String applicantEmail) {
                UserAuth userAuth = userAuthRepository.findByEmail(applicantEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User student = userAuth.getUser();

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                Optional<ClubModerator> existingModerator = clubModeratorRepository.findByClubIdAndUserId(clubId,
                                student.getId());
                if (existingModerator.isPresent()) {
                        ClubModerator moderator = existingModerator.get();
                        if (moderator.getStatus() == ClubModeratorStatus.PENDING_APPLICATION) {
                                throw new ApiException(ResponseCode.USER_HAS_ALREADY_APPLIED);
                        } else if (moderator.getStatus() == ClubModeratorStatus.INVITED) {
                                throw new ApiException(ResponseCode.USER_HAS_ALREADY_BEEN_INVITED);
                        } else if (moderator.getStatus() == ClubModeratorStatus.ACTIVE) {
                                throw new ApiException(ResponseCode.USER_ALREADY_MODERATOR);
                        }

                        moderator.setStatus(ClubModeratorStatus.PENDING_APPLICATION);
                        moderator = clubModeratorRepository.save(moderator);
                        return new ApplyManageResponse(clubMapper.toClubModeratorDTO(moderator));
                }

                ClubModerator application = new ClubModerator(club, student, ClubRole.CLUB_MODERATOR,
                                ClubModeratorStatus.PENDING_APPLICATION);
                application = clubModeratorRepository.save(application);

                List<ClubModerator> admins = clubModeratorRepository.findAllByClubIdAndRoleAndStatus(
                                clubId, ClubRole.CLUB_ADMIN, ClubModeratorStatus.ACTIVE);
                for (ClubModerator admin : admins) {
                        notificationService.sendNotification(admin.getUser(), student,
                                        NotificationType.APPLY_CLUB_MANAGE,
                                        clubId);
                }

                return new ApplyManageResponse(clubMapper.toClubModeratorDTO(application));
        }

        @Transactional
        public ApproveApplicationResponse approveApplication(UUID clubId, UUID applicantId, String approverEmail) {
                UserAuth approverAuth = userAuthRepository.findByEmail(approverEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User approver = approverAuth.getUser();

                // Verify sender is CLUB_ADMIN
                boolean isSenderAdmin = clubModeratorRepository.findByClubIdAndUserId(clubId, approver.getId())
                                .map(m -> m.getRole() == ClubRole.CLUB_ADMIN
                                                && m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isSenderAdmin) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                if (!clubRepository.existsById(clubId)) {
                        throw new ApiException(ResponseCode.CLUB_NOT_FOUND);
                }

                User user = userRepository.findById(applicantId)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                ClubModerator application = clubModeratorRepository.findByClubIdAndUserId(clubId, user.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.MODERATOR_NOT_FOUND));

                if (application.getStatus() != ClubModeratorStatus.PENDING_APPLICATION) {
                        throw new ApiException(ResponseCode.INVALID_APPLICATION_STATUS);
                }

                application.setStatus(ClubModeratorStatus.ACTIVE);
                application = clubModeratorRepository.save(application);

                // Notify applicant
                notificationService.sendNotification(user, approver, NotificationType.APPROVE_CLUB_APPLICATION, clubId);

                return new ApproveApplicationResponse(clubMapper.toClubModeratorDTO(application));
        }

        @Transactional
        public RejectApplicationResponse rejectApplication(UUID clubId, UUID applicantId, String rejectorEmail) {
                UserAuth rejectorAuth = userAuthRepository.findByEmail(rejectorEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User rejector = rejectorAuth.getUser();

                boolean isSenderAdmin = clubModeratorRepository.findByClubIdAndUserId(clubId, rejector.getId())
                                .map(m -> m.getRole() == ClubRole.CLUB_ADMIN
                                                && m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isSenderAdmin) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                if (!clubRepository.existsById(clubId)) {
                        throw new ApiException(ResponseCode.CLUB_NOT_FOUND);
                }

                User user = userRepository.findById(applicantId)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                ClubModerator application = clubModeratorRepository.findByClubIdAndUserId(clubId, user.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.MODERATOR_NOT_FOUND));

                if (application.getStatus() != ClubModeratorStatus.PENDING_APPLICATION) {
                        throw new ApiException(ResponseCode.INVALID_APPLICATION_STATUS);
                }

                application.setStatus(ClubModeratorStatus.REJECTED);
                application = clubModeratorRepository.save(application);

                notificationService.sendNotification(user, rejector, NotificationType.REJECT_CLUB_APPLICATION, clubId);

                return new RejectApplicationResponse(clubMapper.toClubModeratorDTO(application));
        }

        @Transactional
        public void removeModerator(UUID clubId, UUID moderatorId, String requesterEmail) {
                UserAuth requesterAuth = userAuthRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User requester = requesterAuth.getUser();

                boolean isSenderAdmin = clubModeratorRepository.findByClubIdAndUserId(clubId, requester.getId())
                                .map(m -> m.getRole() == ClubRole.CLUB_ADMIN
                                                && m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isSenderAdmin) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                User user = userRepository.findById(moderatorId)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                ClubModerator moderator = clubModeratorRepository.findByClubIdAndUserId(clubId, user.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.MODERATOR_NOT_FOUND));

                boolean isTargetActive = moderator.getStatus() == ClubModeratorStatus.ACTIVE;
                if (!isTargetActive) {
                        throw new ApiException(ResponseCode.CANNOT_REMOVE_INACTIVE_MODERATOR);
                }

                moderator.setStatus(ClubModeratorStatus.REVOKED);
                clubModeratorRepository.save(moderator);
        }

        @Transactional
        public GetFollowedClubsResponse getFollowedClubs(String email, int page, int size) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Club> followedClubs = clubRepository.findFollowedClubs(user.getId(), ClubFollowerStatus.ACTIVE,
                                pageable);

                List<ClubDTO> clubs = followedClubs.stream()
                                .map(clubMapper::toClubDTO)
                                .toList();

                return new GetFollowedClubsResponse(clubs);
        }

        @Transactional
        public GetManagedClubsResponse getManagedClubs(String email, int page, int size) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Club> managedClubs = clubRepository.findManagedClubs(user.getId(), ClubModeratorStatus.ACTIVE,
                                pageable);

                List<ClubDTO> clubs = managedClubs.stream()
                                .map(clubMapper::toClubDTO)
                                .toList();

                return new GetManagedClubsResponse(clubs);
        }

        @Transactional(readOnly = true)
        public GetAllClubsResponse getAllClubs(int page, int size, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Club> clubsPage = clubRepository.findAll(pageable);

                List<UUID> clubIds = clubsPage.getContent().stream().map(Club::getId).toList();
                List<UUID> followedClubIds = clubFollowerRepository.findAllByUserIdAndClubIdInAndStatus(
                                user.getId(), clubIds, ClubFollowerStatus.ACTIVE)
                                .stream().map(cf -> cf.getClub().getId()).toList();

                List<UUID> managedClubIds = clubModeratorRepository.findAllByUserIdAndClubIdInAndStatus(
                                user.getId(), clubIds, ClubModeratorStatus.ACTIVE)
                                .stream().map(cm -> cm.getClub().getId()).toList();

                List<ClubWithStatusDTO> clubWithStatusDTOS = clubsPage.getContent().stream()
                                .map(club -> new ClubWithStatusDTO(clubMapper.toClubDTO(club),
                                                followedClubIds.contains(club.getId()),
                                                managedClubIds.contains(club.getId())))
                                .toList();

                return new GetAllClubsResponse(clubWithStatusDTOS);
        }

        @Transactional(readOnly = true)
        public SearchClubsResponse searchClubs(String keyword, int page, int size, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                if (keyword == null || keyword.trim().isEmpty()) {
                        return new SearchClubsResponse(List.of());
                }

                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Club> clubsPage = clubRepository.searchByName(keyword, pageable);

                List<UUID> clubIds = clubsPage.getContent().stream().map(Club::getId).toList();
                List<UUID> followedClubIds = clubFollowerRepository.findAllByUserIdAndClubIdInAndStatus(
                                user.getId(), clubIds, ClubFollowerStatus.ACTIVE)
                                .stream().map(cf -> cf.getClub().getId()).toList();

                List<UUID> managedClubIds = clubModeratorRepository.findAllByUserIdAndClubIdInAndStatus(
                                user.getId(), clubIds, ClubModeratorStatus.ACTIVE)
                                .stream().map(cm -> cm.getClub().getId()).toList();

                List<ClubWithStatusDTO> clubWithStatusDTOS = clubsPage.getContent().stream()
                                .map(club -> new ClubWithStatusDTO(clubMapper.toClubDTO(club),
                                                followedClubIds.contains(club.getId()),
                                                managedClubIds.contains(club.getId())))
                                .toList();

                return new SearchClubsResponse(clubWithStatusDTOS);
        }

        @Transactional(readOnly = true)
        public GetClubResponse getClub(UUID clubId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                boolean isFollowed = clubFollowerRepository.existsByClubIdAndUserId(clubId, user.getId())
                                && clubFollowerRepository.findByClubIdAndUserId(clubId, user.getId()).get()
                                                .getStatus() == ClubFollowerStatus.ACTIVE;

                boolean isManaged = clubModeratorRepository.findByClubIdAndUserId(clubId, user.getId())
                                .map(m -> m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);

                return new GetClubResponse(
                                new ClubWithStatusDTO(clubMapper.toClubDTO(club), isFollowed, isManaged));
        }

        @Transactional(readOnly = true)
        public GetActiveClubModeratorsResponse getActiveClubModerators(UUID clubId, String email) {
                userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                List<ClubModerator> clubModerators = clubModeratorRepository.findByClubIdAndStatus(clubId, ClubModeratorStatus.ACTIVE);

                return new GetActiveClubModeratorsResponse(
                                clubModerators.stream().map(clubMapper::toClubModeratorDTO).toList());
        }

        @Transactional
        public void deleteClub(UUID clubId, String requesterEmail) {
                UserAuth requesterAuth = userAuthRepository.findByEmail(requesterEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User requester = requesterAuth.getUser();

                boolean isSenderAdmin = clubModeratorRepository.findByClubIdAndUserId(clubId, requester.getId())
                                .map(m -> m.getRole() == ClubRole.CLUB_ADMIN
                                                && m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isSenderAdmin) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                if (!clubRepository.existsById(clubId)) {
                        throw new ApiException(ResponseCode.CLUB_NOT_FOUND);
                }

                // Delete Event Participants
                List<Event> events = eventRepository.findByClubId(clubId);
                List<UUID> eventIds = events.stream().map(Event::getId).toList();
                if (!eventIds.isEmpty()) {
                        eventParticipantRepository.deleteByEventIdIn(eventIds);
                }

                // Delete Events
                eventRepository.deleteByClubId(clubId);

                // Delete Moderators
                clubModeratorRepository.deleteByClubId(clubId);

                // Delete Followers
                clubFollowerRepository.deleteByClubId(clubId);

                // Delete Club
                clubRepository.deleteById(clubId);
        }
}
