package eugenestellar.authservice.controller;

import eugenestellar.authservice.exception.NotFoundRefreshTokenException;
import eugenestellar.authservice.model.dto.AuthLoginUserDto;
import eugenestellar.authservice.model.dto.AuthRegisterUserDto;
import eugenestellar.authservice.model.dto.ResponseTokenDto;
import eugenestellar.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<ResponseTokenDto> register(@Valid @RequestBody AuthRegisterUserDto userDto) { // @Valid validates dto fields

    ResponseTokenDto responseTokenDto = authService.register(userDto);

    return ResponseEntity.ok().body(responseTokenDto);
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseTokenDto> login(@Valid @RequestBody AuthLoginUserDto userDto) {

    ResponseTokenDto responseTokenDto = authService.login(userDto);
    // ResponseCookie cookie = authService.setRefreshTokenInCookie(authService.getUsername(userDto.getEmail()));

    return ResponseEntity.ok().body(responseTokenDto);
  }

  // добавить проверку срока работы токена и если остается меньше недели то слать новый рефреш токен
  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refresh(@CookieValue(name = "refresh-token", required = false) String refreshToken) {

    if (refreshToken == null || refreshToken.isBlank())
      throw new NotFoundRefreshTokenException("Refresh token wasn't provided");

    String accessToken = authService.getNewAccessToken(refreshToken);

    return ResponseEntity.ok().body(Map.of("access-token", accessToken));
  }

//  Logout is implemented on the Frontend side(Next.js)
//    @PostMapping("/logout")
//  public ResponseEntity<Map<String, String>> logout() {
//
//    SecurityContextHolder.clearContext(); // just in case
//
//    ResponseCookie deleteCookie = ResponseCookie.from("refresh-token")
//        .httpOnly(true)
//        .secure(true)
//        .sameSite("None")
//        .path("/")
//        .maxAge(0)
//        .build();
//
//    return ResponseEntity.ok()
//        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
//        .body(Map.of("message","Logged out successfully"));
//  }
}