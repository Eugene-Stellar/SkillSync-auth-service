package eugenestellar.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginUserDto {

  @Email(message = "Email must be valid")
  @NotBlank(message = "Email cannot be empty")
  @Size(max = 255, message = "Email is too long")
  private String email;

  @NotBlank(message = "Password can't be empty")
  @Size(max = 32, min = 6, message = "Password length must be between 6 and 32 characters")
  private String password;
}