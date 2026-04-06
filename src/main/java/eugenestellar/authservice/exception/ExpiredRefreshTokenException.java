package eugenestellar.authservice.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
  public ExpiredRefreshTokenException(String message) {
    super(message);
  }
}
