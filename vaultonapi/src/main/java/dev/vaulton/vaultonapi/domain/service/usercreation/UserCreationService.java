package dev.vaulton.vaultonapi.domain.service.usercreation;

import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationInput;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationResult;
import java.util.UUID;

public interface UserCreationService {
  UUID preRegister();

  UserCreationResult createUser(UserCreationInput input);
}
