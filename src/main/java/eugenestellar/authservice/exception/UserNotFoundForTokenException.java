package eugenestellar.authservice.exception;

public class UserNotFoundForTokenException extends RuntimeException {
  public UserNotFoundForTokenException(String message) {
    super(message);
  }
}
