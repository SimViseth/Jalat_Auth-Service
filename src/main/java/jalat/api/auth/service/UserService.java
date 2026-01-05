package jalat.api.auth.service;

import jakarta.mail.MessagingException;
import jalat.api.auth.dto.request.ForgetRequest;
import jalat.api.auth.dto.request.LoginRequest;
import jalat.api.auth.dto.request.RegisterRequest;
import jalat.api.auth.dto.response.AuthResponse;
import jalat.api.auth.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse register(RegisterRequest registerRequest)throws MessagingException;

    AuthResponse login(LoginRequest loginRequest);

    boolean verifyOtp(String otpCode);

    String resendOtp(String email) throws MessagingException;

    UserResponse forgetPassword(ForgetRequest forgetRequest, String email);

    UUID getUsernameOfCurrentUser();

}
