package eugenestellar.authservice.exception;

public class NotFoundUserOrIncorrectPasswordException extends RuntimeException {
  public NotFoundUserOrIncorrectPasswordException(String message) {
    super(message);
  }
}
