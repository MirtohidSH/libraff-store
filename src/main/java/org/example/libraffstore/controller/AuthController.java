package org.example.libraffstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.AuthRequest;
import org.example.libraffstore.dto.request.TokenRequest;
import org.example.libraffstore.dto.response.AuthResponse;
import org.example.libraffstore.service.UserDetailsServiceImpl;
import org.example.libraffstore.utils.JwtUtil;
import org.example.libraffstore.utils.RefreshTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/apis")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final RefreshTokenUtil refreshTokenUtil;

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getFIN(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getFIN());
		final String jwt = jwtUtil.generateToken(userDetails);
		final String refreshToken = refreshTokenUtil.generateRefreshToken(userDetails);

		return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody TokenRequest tokenRequest) {
		String refreshToken = tokenRequest.getRefreshToken();

		String username = refreshTokenUtil.extractUsername(refreshToken);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (refreshTokenUtil.validateToken(refreshToken, userDetails)) {
			final String newAccessToken = jwtUtil.generateToken(userDetails);
			return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken)); // Keep the same refresh token
		} else {
			return ResponseEntity.status(403).body("Invalid refresh token");
		}
	}

	@GetMapping("/add")
	@PreAuthorize(value = "hasAuthority('ROLE_ADD')")
	public String addData() {
		return "add success";
	}

	@GetMapping("/get")
	@PreAuthorize(value = "hasAuthority('ROLE_GET')")
	public String getData() {
		return "get success";
	}

	@GetMapping("/update")
	@PreAuthorize(value = "hasAuthority('ROLE_UPDATE')")
	public String updateData() {
		return "update success";
	}

	@GetMapping("/delete")
	@PreAuthorize(value = "hasAuthority('ROLE_DELETE')")
	public String deleteData() {
		return "delete success";
	}

}
