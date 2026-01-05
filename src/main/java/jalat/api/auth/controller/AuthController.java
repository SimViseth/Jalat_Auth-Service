package jalat.api.auth.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jalat.api.auth.app.APIResponse;
import jalat.api.auth.dto.request.ForgetRequest;
import jalat.api.auth.dto.request.LoginRequest;
import jalat.api.auth.dto.request.RegisterRequest;
import jalat.api.auth.dto.response.AuthResponse;
import jalat.api.auth.dto.response.UserResponse;
import jalat.api.auth.exception.InvalidInputException;
import jalat.api.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor

public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserResponse>> register(@RequestBody @Valid RegisterRequest registerRequest) throws MessagingException {
        if (!isValidPassword(registerRequest.getPassword()))
            throw new InvalidInputException("Password must be at least 8 characters long and contain at least one digit, one letter, and one special character.");
        UserResponse userResponse = userService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(
                "Please Check Email for Verify OTP Code", userResponse, HttpStatus.CREATED, new Date()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        AuthResponse response = userService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(
                "Login Successful", response, HttpStatus.CREATED, new Date()
        ));
    }

    @PutMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam @Positive(message = "OTP code must be a positive number") String otpCode) {
        boolean response = userService.verifyOtp(otpCode);
        return ResponseEntity.status(HttpStatus.OK).body(new APIResponse<>(
                "Your account is Verify successfully", response, HttpStatus.OK, new Date()
        ));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam @Valid String email) throws MessagingException {
        String message = userService.resendOtp(email);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PutMapping("/forget-password")
    public ResponseEntity<UserResponse> forgetPassword(@RequestBody @Valid ForgetRequest forgetRequest, @RequestParam @Valid String email) {
        if (!isValidPassword(forgetRequest.getPassword())) throw new InvalidInputException("Password must be at least 8 characters long and contain at least one digit, one letter, and one special character.");

        UserResponse user  = userService.forgetPassword(forgetRequest, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public static boolean isValidPassword(String password){
        return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    }
}
