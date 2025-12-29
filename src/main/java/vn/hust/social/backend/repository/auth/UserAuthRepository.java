package vn.hust.social.backend.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.entity.user.UserAuth.AuthProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {

    Optional<UserAuth> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserAuth> findByProviderAndEmail(AuthProvider provider, String email);

    boolean existsByProviderAndEmail(AuthProvider authProvider, String email);

    List<UserAuth> findByUserId(UUID userId);
}
