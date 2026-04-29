package org.example.libraffstore.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest{

    @NotBlank(message = "FIN boş ola bilməz")
    private String FIN;

    @NotBlank(message = "Şifrə boş ola bilməz")
    private String password;
}
