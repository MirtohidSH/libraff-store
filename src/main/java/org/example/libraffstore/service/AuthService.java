package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.AuthRequest;
import org.example.libraffstore.dto.request.TokenRequest;
import org.example.libraffstore.dto.response.AuthResponse;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.utils.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getFIN(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken  = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(TokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsernameFromRefreshToken(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.validateRefreshToken(refreshToken, userDetails))
            throw new BusinessException("Refresh token etibarsızdır.");

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        return new AuthResponse(newAccessToken, refreshToken);
    }
}