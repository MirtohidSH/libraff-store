package org.example.libraffstore.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.auth.dto.AuthRequest;
import org.example.libraffstore.auth.dto.TokenRequest;
import org.example.libraffstore.auth.dto.AuthResponse;
import org.example.libraffstore.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRequest request) {
		return ResponseEntity.ok(authService.refresh(request));
	}


}