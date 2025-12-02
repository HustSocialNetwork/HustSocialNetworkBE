package vn.hust.social.backend.service.user.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.dto.post.PostDTO;
import vn.hust.social.backend.dto.post.get.GetPostsResponse;
import vn.hust.social.backend.dto.user.ProfileDTO;
import vn.hust.social.backend.dto.user.profile.GetMeProfileResponse;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.mapper.PostMapper;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.repository.user.UserAuthRepository;
import vn.hust.social.backend.repository.user.UserRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserAuthRepository userAuthRepository;
    private final UserMapper userMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Transactional
    public GetMeProfileResponse getMeProfile(String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        User user = userAuth.getUser();
        ProfileDTO profileDTO = userMapper.toProfileDTO(user);

        return new GetMeProfileResponse(profileDTO);
    }

    @Transactional
    public GetPostsResponse getPosts(String userId, int page, int pageSize, String email) {
        UserAuth viewerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Viewer not found"));
        UUID userID =  UUID.fromString(userId);
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findPostsByUser(user, pageable);
        List<PostDTO> postDTOS = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        return new GetPostsResponse(postDTOS);
    }
}
