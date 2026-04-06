package eugenestellar.authservice.controller;

import eugenestellar.authservice.util.RsaKeyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DiscoveryController {

  private final RsaKeyManager keyManager;
  private final String issuerUrl;

  public DiscoveryController(RsaKeyManager keyManager, @Value("${ISSUER_URL}") String issuerUrl) {
    this.keyManager = keyManager;
    this.issuerUrl = issuerUrl;
  }

  @GetMapping("/.well-known/openid-configuration")
  public Map<String, Object> getOidcConfiguration() {
    return Map.of(
        "issuer", issuerUrl,
        "jwks_uri", issuerUrl + "/.well-known/jwks.json",
        "id_token_signing_alg_values_supported", List.of("RS256")
    );
  }

  @GetMapping("/.well-known/jwks.json")
  public Map<String, Object> getJwks() {
    RSAPublicKey publicKey = keyManager.getPublicKey();

    Map<String, Object> key = new HashMap<>();
    key.put("kty", "RSA");
    key.put("kid", "skillsync-key-1");
    key.put("use", "sig");
    key.put("alg", "RS256");

    // Перевод ключа в формат Base64Url (требование стандарта OIDC)
    key.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray()));
    key.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray()));

    return Map.of("keys", List.of(key));
  }
}