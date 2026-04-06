package eugenestellar.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class}) // to shun a default password from SpringSecurity
public class AuthServiceApplication {
	public static void main(String[] args) {
		decodeCertificates();
		SpringApplication.run(AuthServiceApplication.class, args);
	}
	private static void decodeCertificates() {
		try {
			Files.createDirectories(Paths.get("/tmp/certs"));

			decodeAndSave("KAFKA_KEYSTORE_BASE64", "/tmp/certs/client.keystore.p12");
			decodeAndSave("KAFKA_TRUSTSTORE_BASE64", "/tmp/certs/client.truststore.jks");
			decodeAndSave("RSA_PUBLIC_BASE64", "/tmp/certs/public.pem");
			decodeAndSave("RSA_PRIVATE_BASE64", "/tmp/certs/private.pem");

		} catch (Exception e) {
			System.err.println("Failed to decode certificates: " + e.getMessage());
		}
	}

	private static void decodeAndSave(String envVar, String filePath) throws IOException {
		String base64Content = System.getenv(envVar);
		if (base64Content != null && !base64Content.isBlank()) {
			byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
			Files.write(Paths.get(filePath), decodedBytes);
		}
	}
}