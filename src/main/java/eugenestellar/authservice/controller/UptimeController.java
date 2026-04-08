package eugenestellar.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uptime")
public class UptimeController {
  @GetMapping
  public ResponseEntity<?> uptime() {
    return ResponseEntity.ok().build();
  }
}