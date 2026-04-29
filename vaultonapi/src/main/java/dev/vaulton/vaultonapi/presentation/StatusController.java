package dev.vaulton.vaultonapi.presentation;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

  @GetMapping("/api/status")
  public Map<String, String> getStatus() {
    return Map.of(
        "status", "UP",
        "message", "Vaulton Java API is running",
        "environment", "development");
  }
}
