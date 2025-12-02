package vn.hust.social.backend.service.user.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.repository.user.UserAuthRepository;
import vn.hust.social.backend.repository.user.UserRepository;

import java.time.Duration;
import java.util.UUID;

@Service
public class EmailVerificationService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final UserAuthRepository userAuthRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(StringRedisTemplate redisTemplate, EmailService emailService, UserAuthRepository userAuthRepository, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
        this.userAuthRepository = userAuthRepository;
        this.userRepository = userRepository;
    }

    public void sendVerificationEmail(String email, String name) {
        String token = UUID.randomUUID().toString();
        String key = "verify:email:" + token;

        redisTemplate.opsForValue().set(key, email, Duration.ofMinutes(30));

        String verifyLink = baseUrl + "/api/auth/verify-email?token=" + token;

        try {
            emailService.sendVerificationEmail(email, name, verifyLink);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public boolean verifyEmailToken(String token) {
        String key = "verify:email:" + token;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            return false;
        }

        redisTemplate.delete(key);

        User user = userAuthRepository.findByEmail(email).get().getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return true;
    }
}

