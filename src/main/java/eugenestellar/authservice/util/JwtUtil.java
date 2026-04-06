package eugenestellar.authservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

  private final Algorithm algorithm;
  private final String issuerUrl;

  public JwtUtil(RsaKeyManager keyManager, @Value("${ISSUER_URL}") String issuerUrl) {
    // Инициализируем RSA алгоритм двумя ключами
    this.algorithm = Algorithm.RSA256(keyManager.getPublicKey(), keyManager.getPrivateKey());
    this.issuerUrl = issuerUrl;
  }


  public String generateToken(Long userId, String username, boolean accessToken, List<String> roles) {
    Date expirationDate = accessToken ? Date.from(ZonedDateTime.now().plusMinutes(15).toInstant())
        : Date.from(ZonedDateTime.now().plusDays(30).toInstant());

    var jwtBuilder = JWT.create() // JWTCreator.Builder is operational as well
        .withSubject(username)
        .withClaim("type", accessToken ? "access" : "refresh")
        .withIssuedAt(new Date())
        .withIssuer(issuerUrl)
        .withAudience("skillsync-api")
        .withKeyId("skillsync-key-1")
        .withExpiresAt(expirationDate);

    if (accessToken && userId != null && roles != null && !roles.isEmpty()) { // roles & UserId for access token
      jwtBuilder.withClaim("roles", roles);
      jwtBuilder.withClaim("userId", userId);
    }

    return jwtBuilder.sign(algorithm);
  }

  public DecodedJWT validateAccessToken(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT
        .require(algorithm)
        .withIssuer(issuerUrl)
        .withClaim("type", "access")
        .acceptLeeway(60)
        .build();

    return verifier.verify(token);
  }

  public DecodedJWT validateRefreshToken(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT
        .require(algorithm)
        .withIssuer(issuerUrl)
        .withClaim("type", "refresh")
        .acceptLeeway(60)
        .build();

    return verifier.verify(token);
  }
}