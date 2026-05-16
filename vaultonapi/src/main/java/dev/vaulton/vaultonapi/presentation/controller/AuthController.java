package dev.vaulton.vaultonapi.presentation.controller;

import dev.vaulton.vaultonapi.application.dto.auth.requests.RegisterRequest;
import dev.vaulton.vaultonapi.application.dto.auth.responses.PreRegisterResponse;
import dev.vaulton.vaultonapi.application.dto.auth.responses.RegisterResponse;
import dev.vaulton.vaultonapi.application.usecase.auth.preregister.PreRegisterUseCase;
import dev.vaulton.vaultonapi.application.usecase.auth.register.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final PreRegisterUseCase preRegisterUseCase;
  private final RegisterUserUseCase registerUserUseCase;

  @PostMapping("/pre-register")
  public ResponseEntity<PreRegisterResponse> preRegister() {
    return ResponseEntity.ok(preRegisterUseCase.execute());
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
    try (request) {
      RegisterResponse response = registerUserUseCase.execute(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
  }
}
