package eugenestellar.authservice.model.dto;

public record UserRegisteredEventDto(
    Long userId,
    String username,
    String role,
    String email
) {}