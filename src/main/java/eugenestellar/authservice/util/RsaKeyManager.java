package eugenestellar.authservice.util;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyManager {

  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;

  @PostConstruct
  public void init() throws Exception {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    String publicKeyPEM = new String(new ClassPathResource("/certs/public.pem").getInputStream().readAllBytes())
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");

    byte[] encodedPublic = Base64.getDecoder().decode(publicKeyPEM);
    this.publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublic));

    String privateKeyPEM = new String(new ClassPathResource("certs/private.pem").getInputStream().readAllBytes())
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
    byte[] encodedPrivate = Base64.getDecoder().decode(privateKeyPEM);
    this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivate));
  }

  public RSAPublicKey getPublicKey() {
    return publicKey;
  }

  public RSAPrivateKey getPrivateKey() {
    return privateKey;
  }

}