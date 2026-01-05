package jalat.api.auth.repository;

import jalat.api.auth.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findFirstByUserIdOrderByExpirationTimeDesc(UUID userId);

    Optional<Otp> findFirstByOtpCodeAndVerifiedFalseOrderByExpirationTimeDesc(String otpCode);

    @Query("""
        SELECT o FROM Otp o
        WHERE o.userId = (
            SELECT u.userId FROM User u WHERE u.email = :email
        )
        AND o.verified = false
        ORDER BY o.expirationTime DESC
    """)
    Optional<Otp> findLatestUnverifiedOtpByEmail(@Param("email") String email);
}
