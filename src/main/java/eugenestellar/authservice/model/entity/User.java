package eugenestellar.authservice.model.entity;

import eugenestellar.authservice.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(20) CHECK (length(username) >= 3)") // db level
  private String username;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(unique = true, nullable = false)
  @Email(message = "Email must be valid")
  @NotBlank(message = "Email cannot be empty")
  @Size(max = 255, message = "Email is too long")
  private String email;
}