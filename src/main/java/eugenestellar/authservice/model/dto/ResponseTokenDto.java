package eugenestellar.authservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTokenDto {
  @JsonProperty("access-token")
  private String accessToken;
  @JsonProperty("refresh-token")
  private String refreshToken;
}