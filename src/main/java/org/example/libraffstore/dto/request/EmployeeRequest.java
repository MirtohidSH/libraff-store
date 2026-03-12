package org.example.libraffstore.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRequest {

    @NotBlank(message = "FIN boş ola bilməz")
    @Size(min = 7, max = 7, message = "FIN 7 simvol olmalıdır")
    private String FIN;

    @NotBlank(message = "Ad boş ola bilməz")
    private String firstName;

    @NotBlank(message = "Soyad boş ola bilməz")
    private String lastName;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə minimum 6 simvol olmalıdır")
    private String password;

    private Boolean isActive = false;

    @Email(message = "Email düzgün formatda deyil")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Telefon nömrəsi düzgün deyil")
    private String phone;

    @NotNull(message = "Maaş boş ola bilməz")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maaş 0-dan böyük olmalıdır")
    private BigDecimal salary;

    @NotNull(message = "İşə başlama tarixi boş ola bilməz")
    private LocalDate dateEmployed;

    private LocalDate dateUnemployed;

    @NotNull(message = "Mağaza ID boş ola bilməz")
    private Long storeId;

    @NotNull(message = "Vəzifə ID boş ola bilməz")
    private Long positionId;
}