package org.example.libraffstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.AuthRequest;
import org.example.libraffstore.dto.request.TokenRequest;
import org.example.libraffstore.dto.response.AuthResponse;
import org.example.libraffstore.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_ADD')")
	public String addData() {
		return "add success";
	}

	@GetMapping("/get")
	@PreAuthorize("hasAuthority('ROLE_GET')")
	public String getData() {
		return "get success";
	}

	@GetMapping("/update")
	@PreAuthorize("hasAuthority('ROLE_UPDATE')")
	public String updateData() {
		return "update success";
	}

	@GetMapping("/delete")
	@PreAuthorize("hasAuthority('ROLE_DELETE')")
	public String deleteData() {
		return "delete success";
	}
}