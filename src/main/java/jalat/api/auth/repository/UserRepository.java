package jalat.api.auth.repository;

import jalat.api.auth.dto.response.UserResponse;
import jalat.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    // Only needed if you want projection (UserResponse) directly
    @Query("""
        SELECT new jalat.api.auth.dto.response.UserResponse(u.userId, u.email, u.profileImage)
        FROM User u
        WHERE u.userId = :userId
    """)
    Optional<UserResponse> findUserResponseById(@Param("userId") UUID userId);
}
