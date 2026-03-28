package com.drivemond.auth.service;

import com.drivemond.auth.dto.AuthResponse;
import com.drivemond.auth.dto.LoginRequest;
import com.drivemond.auth.dto.UserSummaryDto;
import com.drivemond.auth.entity.User;
import com.drivemond.auth.repository.UserRepository;
import com.drivemond.auth.security.JwtProperties;
import com.drivemond.auth.security.JwtService;
import com.drivemond.core.exception.DrivemondException;
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
            throw DrivemondException.badRequest("Invalid credentials");
        }

        User user = userRepository.findByEmailOrPhone(
                request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> DrivemondException.notFound("User not found"));

        if (!user.isEnabled()) {
            throw DrivemondException.forbidden("Your account is inactive");
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
            throw DrivemondException.badRequest("Not a refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmailOrPhone(username, username)
                .orElseThrow(() -> DrivemondException.notFound("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw DrivemondException.badRequest("Refresh token is expired or invalid");
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
