package dev.vaulton.vaultonapi.application.usecase.auth.register;

import dev.vaulton.vaultonapi.application.dto.auth.requests.RegisterRequest;
import dev.vaulton.vaultonapi.application.dto.auth.responses.RegisterResponse;

public interface RegisterUserUseCase {
  RegisterResponse execute(RegisterRequest request);
}
