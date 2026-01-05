package jalat.api.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID userId;
    private String email;
    private String profileImage;
    public UserResponse(UUID userId, String email, String profileImage) {
        this.userId = userId;
        this.email = email;
        this.profileImage = profileImage;
    }
}
