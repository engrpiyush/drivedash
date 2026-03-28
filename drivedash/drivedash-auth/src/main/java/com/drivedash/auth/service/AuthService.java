package com.drivedash.auth.service;

import com.drivedash.auth.dto.AuthResponse;
import com.drivedash.auth.dto.LoginRequest;
import com.drivedash.auth.dto.UserSummaryDto;
import com.drivedash.auth.entity.User;
import com.drivedash.auth.repository.UserRepository;
import com.drivedash.auth.security.JwtProperties;
import com.drivedash.auth.security.JwtService;
import com.drivedash.core.exception.DrivedashException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles login, token refresh, and logout logic.
 * Replaces Laravel's {@code AuthManagement} module controllers and services.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw DrivedashException.badRequest("Invalid credentials");
        }

        User user = userRepository.findByEmailOrPhone(
                request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> DrivedashException.notFound("User not found"));

        if (!user.isEnabled()) {
            throw DrivedashException.forbidden("Your account is inactive");
        }

        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken,
                jwtProperties.getExpirationMs() / 1000,
                toUserSummary(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw DrivedashException.badRequest("Not a refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmailOrPhone(username, username)
                .orElseThrow(() -> DrivedashException.notFound("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw DrivedashException.badRequest("Refresh token is expired or invalid");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        return AuthResponse.of(newAccessToken, refreshToken,
                jwtProperties.getExpirationMs() / 1000,
                toUserSummary(user));
    }

    private UserSummaryDto toUserSummary(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .userType(user.getUserType())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
