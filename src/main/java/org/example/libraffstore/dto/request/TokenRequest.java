package org.example.libraffstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {

	@NotBlank(message = "Refresh token boş ola bilməz")
	private String refreshToken;
}
