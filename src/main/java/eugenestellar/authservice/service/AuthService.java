package eugenestellar.authservice.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import eugenestellar.authservice.exception.ExpiredRefreshTokenException;
import eugenestellar.authservice.exception.NotFoundUserOrIncorrectPasswordException;
import eugenestellar.authservice.exception.UserNotFoundForTokenException;
import eugenestellar.authservice.exception.UsernameOrEmailAlreadyExistException;
import eugenestellar.authservice.model.Role;
import eugenestellar.authservice.model.dto.AuthLoginUserDto;
import eugenestellar.authservice.model.dto.AuthRegisterUserDto;
import eugenestellar.authservice.model.dto.ResponseTokenDto;
import eugenestellar.authservice.model.dto.UserRegisteredEventDto;
import eugenestellar.authservice.repository.UserRepo;
import eugenestellar.authservice.util.JwtUtil;
import eugenestellar.authservice.model.entity.User;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final KafkaProducerService kafkaProducerService;

  public AuthService(UserRepo userRepo, KafkaProducerService kafkaProducerService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepo = userRepo;
    this.kafkaProducerService = kafkaProducerService;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public ResponseTokenDto register(AuthRegisterUserDto userDto) {

    String username = userDto.getUsername();

    if (userRepo.findByUsername(username).isPresent())
      throw new UsernameOrEmailAlreadyExistException("This username has been already taken");

    if (userRepo.findByEmail(userDto.getEmail()).isPresent())
      throw new UsernameOrEmailAlreadyExistException("This email has been already taken");

    // create user in order to save it in DB
    User userForDb = new User();
    userForDb.setPassword(passwordEncoder.encode(userDto.getPassword()));
    userForDb.setUsername(username);
    userForDb.setRole(Role.valueOf(userDto.getRole().toUpperCase()));
    userForDb.setEmail(userDto.getEmail());

    User savedUser = userRepo.save(userForDb); // there will be error from DB in the case if 2 users try to register with the same username

    // sending event to Kafka
    UserRegisteredEventDto event = new UserRegisteredEventDto(
        savedUser.getId(),
        savedUser.getUsername(),
        savedUser.getRole().toString(),
        savedUser.getEmail()
    );
    kafkaProducerService.sendRegistrationEvent(event);

    String token = jwtUtil.generateToken(savedUser.getId(), username,true, List.of(userForDb.getRole().toString()));
    return new ResponseTokenDto(token);
  }


  public ResponseCookie setRefreshTokenInCookie(String username) {

    return ResponseCookie.from("refresh-token", jwtUtil.generateToken(null, username, false, null))
        .httpOnly(true)
        .secure(true)
        .sameSite("None") // for cross-domain access
        .path("/") // cookie scope i.e. which paths will be the cookie send to, /auth by default(matched with Controller path)
        .maxAge(Duration.ofDays(30))
        .build();
  }

  public ResponseTokenDto login(AuthLoginUserDto userDto) {

    String username = userDto.getUsername();

    Optional<User> userFromDbOptional = userRepo.findByUsername(username);

    if (userFromDbOptional.isEmpty())
      throw new NotFoundUserOrIncorrectPasswordException("There's no user with a name " + username);

    User userFromDb = userFromDbOptional.get();

    if (!passwordEncoder.matches(userDto.getPassword(), userFromDb.getPassword()))
      throw new NotFoundUserOrIncorrectPasswordException("The password is incorrect");

    String role = userFromDb.getRole().toString();
    String token = jwtUtil.generateToken(userFromDb.getId(), username, true, List.of(role));

    return new ResponseTokenDto(token);

  }

  public ResponseTokenDto getNewAccessToken(String refreshToken) {
    try {
      DecodedJWT jwt = jwtUtil.validateRefreshToken(refreshToken);
      String username = jwt.getSubject();

      User user = userRepo.findByUsername(username)
          .orElseThrow(() -> new UserNotFoundForTokenException("User not found with username: " + username));

      String role = user.getRole().toString();
      String accessToken = jwtUtil.generateToken(user.getId(), username, true, List.of(role));

      return new ResponseTokenDto(accessToken);

    } catch (JWTVerificationException ex) {
      throw new ExpiredRefreshTokenException("Invalid or expired refresh token");
    }
  }
}