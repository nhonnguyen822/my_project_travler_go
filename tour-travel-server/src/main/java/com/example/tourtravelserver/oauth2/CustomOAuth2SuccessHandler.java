package com.example.tourtravelserver.oauth2;

import com.example.tourtravelserver.entity.Role;
import com.example.tourtravelserver.entity.User;
import com.example.tourtravelserver.enums.CustomerType;
import com.example.tourtravelserver.security.jwt.service.JwtService;
import com.example.tourtravelserver.service.IRoleService;
import com.example.tourtravelserver.service.IUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final IUserService userService;
    private final JwtService jwtService;
    private final IRoleService roleService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // ✅ Lấy thông tin từ Google
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();
        Instant accessTokenExpiry = client.getAccessToken().getExpiresAt();
        String refreshToken = client.getRefreshToken() != null
                ? client.getRefreshToken().getTokenValue()
                : null;

        // ✅ Email bắt buộc phải có
        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by Google");
            return;
        }

        // ✅ Tìm user theo email
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            // 🆕 Nếu user chưa tồn tại → tạo mới
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAvatar(avatarUrl);
            user.setProvider("google");
            user.setProviderId(providerId);
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            user.setAccessTokenExpiry(accessTokenExpiry);
            user.setCustomerCode(user.generateCustomerCode());
            user.setStatus(true);
            user.setCustomerType(CustomerType.REGULAR);
            user.setEmailVerification(true);

            // Gán role mặc định
            Optional<Role> userRole = roleService.findByName("USER");
            if (userRole.isPresent()) {
                user.setRole(userRole.get());
            }

            user = userService.save(user);
        } else {
            // 🔄 Nếu user đã tồn tại → cập nhật thông tin mới
            user.setProvider("google");
            user.setProviderId(providerId);
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            user.setAccessTokenExpiry(accessTokenExpiry);
            if (user.getAvatar() == null && avatarUrl != null) {
                user.setAvatar(avatarUrl);
            }
            user = userService.save(user);
        }

        if (!user.getStatus()) {
            response.sendRedirect(frontendUrl + "/login?error=ACCOUNT_DISABLED");
            return;
        }

        // ✅ Sinh JWT
        String jwtToken = jwtService.generateToken(user);

        // ✅ Tạo cookie JWT
        boolean rememberMe = "true".equalsIgnoreCase(request.getParameter("rememberMe"));
        long maxAge = rememberMe ? 30L * 24 * 60 * 60 : 3600;

        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(frontendUrl + "/oauth2/success");
    }
}
