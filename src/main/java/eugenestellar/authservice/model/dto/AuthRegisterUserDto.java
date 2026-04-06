package eugenestellar.authservice.model.dto;


import com.fasterxml.jackson.annotation.JsonSetter;
import eugenestellar.authservice.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterUserDto {

  @NotBlank(message = "Name can't be empty")
  @Size(max = 20, min = 3, message = "Username length must be between 3 and 20 characters") // code level
  @Pattern(regexp = "^[\\p{L}\\p{N}_]+$", message = "Username can contain only letters, numbers and underscore")
  private String username;

  @NotBlank(message = "Password can't be empty")
  @Size(max = 32, min = 6, message = "Password length must be between 6 and 32 characters")
  private String password;

  @Email(message = "Email must be valid")
  @NotBlank(message = "Email cannot be empty")
  @Size(max = 255, message = "Email is too long")
  private String email;

  @NotBlank(message = "Role can't be empty")
  @Pattern(regexp = "^(?i)(APPLICANT|EMPLOYER)$", message = "Invalid role. Must be APPLICANT or EMPLOYER")
  private String role;
}