package jalat.api.auth.service.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jalat.api.auth.config.PasswordConfig;
import jalat.api.auth.dto.request.ForgetRequest;
import jalat.api.auth.dto.request.LoginRequest;
import jalat.api.auth.dto.request.RegisterRequest;
import jalat.api.auth.dto.response.AuthResponse;
import jalat.api.auth.dto.response.UserResponse;
import jalat.api.auth.entity.Otp;
import jalat.api.auth.entity.User;
import jalat.api.auth.exception.InvalidInputException;
import jalat.api.auth.exception.NotFoundException;
import jalat.api.auth.jwt.JwtService;
import jalat.api.auth.repository.OtpRepository;
import jalat.api.auth.repository.UserRepository;
import jalat.api.auth.service.AuthService;
import jalat.api.auth.service.UserService;
import jalat.api.auth.utils.EmailUtil;
import jalat.api.auth.utils.OtpUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpUtil otpUtil;
    private final EmailUtil emailUtil;
    private final AuthService authService;
    private final PasswordConfig passwordConfig;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) throws MessagingException {

        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(u -> {
                    throw new InvalidInputException("Email already register");
                });

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())
                || registerRequest.getPassword().length() < 8) {
            throw new InvalidInputException("Passwords do not match or have at least 8 characters");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setProfileImage(registerRequest.getProfileImage());

        User savedUser = userRepository.save(user);

        String otpCode = otpUtil.generateOtp();
        Otp otp = new Otp();
        otp.setUserId(savedUser.getUserId());
        otp.setOtpCode(otpCode);
        otp.setIssuedAt(new Timestamp(System.currentTimeMillis()));
        otp.setExpirationTime(calculateExpirationTime());
        otp.setVerified(false);

        otpRepository.save(otp);
        emailUtil.sendOtpEmail(savedUser.getEmail(), otpCode);

        return new UserResponse(savedUser.getUserId(), savedUser.getEmail(), savedUser.getProfileImage());
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Otp latestOtp = otpRepository
                .findFirstByUserIdOrderByExpirationTimeDesc(user.getUserId())
                .orElseThrow(() -> new NotFoundException("Account not verified"));

        if (!latestOtp.isVerified())
            throw new NotFoundException("Your account is not verified yet");

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new NotFoundException("Passwords do not match");

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String otpCode) {

        Otp otp = otpRepository
                .findFirstByOtpCodeAndVerifiedFalseOrderByExpirationTimeDesc(otpCode)
                .orElseThrow(() -> new NotFoundException("OTP is invalid"));

        if (otp.getExpirationTime().before(new Timestamp(System.currentTimeMillis())))
            throw new NotFoundException("OTP expired");

        otp.setVerified(true);
        otpRepository.save(otp);

        return true;
    }

    @Override
    @Transactional
    public String resendOtp(String email) throws MessagingException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Otp otp = otpRepository.findLatestUnverifiedOtpByEmail(email)
                .orElseThrow(() -> new NotFoundException("Account already verified"));

        String newOtpCode = otpUtil.generateOtp();

        otp.setOtpCode(newOtpCode);
        otp.setIssuedAt(new Timestamp(System.currentTimeMillis()));
        otp.setExpirationTime(calculateExpirationTime());

        otpRepository.save(otp);
        emailUtil.sendOtpEmail(email, newOtpCode);

        return "OTP resent successfully.";
    }

    @Override
    @Transactional
    public UserResponse forgetPassword(ForgetRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email not found"));

        Otp otp = otpRepository
                .findFirstByUserIdOrderByExpirationTimeDesc(user.getUserId())
                .orElseThrow(() -> new InvalidInputException("Email not verified"));

        if (!otp.isVerified())
            throw new InvalidInputException("Email not verified");

        if (!request.getPassword().equals(request.getConfirmPassword())
                || request.getPassword().length() < 8)
            throw new InvalidInputException("Invalid password");

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }

    private Timestamp calculateExpirationTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + (2 * 60 * 1000);
        return new Timestamp(expirationTimeMillis);
    }
    @Override
    public UUID getUsernameOfCurrentUser() {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        UUID userId = userDetails.getUserId();
        System.out.println(userId);
        return userId;
    }
}
