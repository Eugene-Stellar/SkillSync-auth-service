package eugenestellar.authservice.exception;

public class UsernameOrEmailAlreadyExistException extends RuntimeException {
  public UsernameOrEmailAlreadyExistException(String message) {
    super(message);
  }
}