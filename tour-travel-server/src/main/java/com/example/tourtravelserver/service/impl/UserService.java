package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.entity.UserToken;
import com.example.tourtravelserver.enums.TokenType;
import com.example.tourtravelserver.repository.IUserRepository;
import com.example.tourtravelserver.service.IMailService;
import com.example.tourtravelserver.service.IUserService;
import com.example.tourtravelserver.util.CloudinaryService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService userTokenService;
    private final IMailService emailService;
    private final CloudinaryService cloudinaryService;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            if (user.getId() == null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void register(User user) {
        try {
            User savedUser = save(user);
            String token = userTokenService.generateToken(savedUser, TokenType.EMAIL_VERIFICATION);
            emailService.sendUserVerificationEmail(savedUser.getEmail(), savedUser.getName(), token);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resendEmailVerification(User user) {
        UserToken userToken = userTokenService.findByUserAndType(user, TokenType.EMAIL_VERIFICATION)
                .get();
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy token"));

        LocalDateTime now = LocalDateTime.now();

        // Nếu token vẫn còn hạn trên 10 phút thì không gửi lại
        if (userToken.getExpiresAt().isAfter(now.plusMinutes(10))) {
//            throw new RuntimeException("Token vẫn còn hạn, chưa cần gửi lại");
            return;
        }

        try {
            // Tạo token mới
            String newToken = userTokenService.generateToken(user, TokenType.EMAIL_VERIFICATION);
            emailService.sendUserVerificationEmail(user.getEmail(), user.getName(), newToken);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email", e);
        }
    }

    @Override
    public String updateAvatar(Long userId, String newAvatar) throws Exception {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        if (user.getAvatar() != null) {
            cloudinaryService.deleteImageByUrl(user.getAvatar());
        }
        String uploadedAvatar = cloudinaryService.uploadImageFromUrl(newAvatar);
        user.setAvatar(uploadedAvatar);
        userRepository.save(user);
        return uploadedAvatar;
    }
}
