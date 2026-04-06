package eugenestellar.authservice.service;

import eugenestellar.authservice.model.dto.UserRegisteredEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

  private final KafkaTemplate<String, UserRegisteredEventDto> kafkaTemplate;

  private static final String TOPIC = "topic-registration";

  public KafkaProducerService(KafkaTemplate<String, UserRegisteredEventDto> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendRegistrationEvent(UserRegisteredEventDto event) {
    String key = String.valueOf(event.userId());

    CompletableFuture<SendResult<String, UserRegisteredEventDto>> future =
        kafkaTemplate.send(TOPIC, key, event);

    future.whenComplete((result, ex) -> {
      if (ex != null) {
        System.err.println("Error occurred during sending to Kafka: " + ex.getMessage());
      }
    });
  }

}