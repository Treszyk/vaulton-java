package dev.vaulton.vaultonapi.application.usecase.auth.preregister;

import dev.vaulton.vaultonapi.application.dto.auth.responses.PreRegisterResponse;
import dev.vaulton.vaultonapi.domain.service.usercreation.UserCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreRegisterUseCaseImpl implements PreRegisterUseCase {
  private final UserCreationService userCreationService;

  @Override
  public PreRegisterResponse execute() {
    return new PreRegisterResponse(userCreationService.preRegister(), 1);
  }
}
