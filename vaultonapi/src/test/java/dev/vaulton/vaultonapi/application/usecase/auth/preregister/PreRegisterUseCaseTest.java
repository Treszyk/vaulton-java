package dev.vaulton.vaultonapi.application.usecase.auth.preregister;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.vaulton.vaultonapi.application.dto.auth.responses.PreRegisterResponse;
import dev.vaulton.vaultonapi.domain.service.usercreation.UserCreationService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreRegisterUseCaseTest {

  @Mock private UserCreationService userCreationService;

  @InjectMocks private PreRegisterUseCaseImpl useCase;

  @Test
  void shouldReturnNewAccountIdFromDomainService() {
    UUID expectedId = UUID.randomUUID();
    when(userCreationService.preRegister()).thenReturn(expectedId);

    PreRegisterResponse response = useCase.execute();

    assertEquals(expectedId, response.accountId());
    assertEquals(1, response.cryptoSchemaVer());
    verify(userCreationService, times(1)).preRegister();
  }
}
