package com.tradingjournal.service;

import com.tradingjournal.dto.AuthRequest;
import com.tradingjournal.dto.AuthResponse;
import com.tradingjournal.dto.RegisterRequest;
import com.tradingjournal.model.User;
import com.tradingjournal.model.enums.Role;
import com.tradingjournal.repository.UserRepository;
import com.tradingjournal.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();
        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(()-> new RuntimeException("Error occurs in register"));

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse.User newUser = AuthResponse.User.builder()
                .username(dbUser.getUsername())
                .email(dbUser.getEmail())
                .role(String.valueOf(dbUser.getRole()))
                .user_id(dbUser.getId())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(newUser)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse.User tmpUser = AuthResponse.User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .user_id(user.getId())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(tmpUser)
                .build();
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken, jwtService.extractUsername(refreshToken))) {
            throw new RuntimeException("Invalid refresh token");
        }
        User user = userRepository.findByEmail(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newAccessToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }


}
