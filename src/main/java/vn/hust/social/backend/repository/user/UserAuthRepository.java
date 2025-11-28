package vn.hust.social.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.user.UserAuth;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {

    Optional<UserAuth> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserAuth> findByProviderAndEmail(UserAuth.AuthProvider provider, String email);

    boolean existsByProviderAndEmail(UserAuth.AuthProvider authProvider, String email);

}
