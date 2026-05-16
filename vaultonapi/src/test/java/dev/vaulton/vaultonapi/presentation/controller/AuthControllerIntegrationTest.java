package dev.vaulton.vaultonapi.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vaulton.vaultonapi.application.dto.auth.requests.RegisterRequest;
import dev.vaulton.vaultonapi.application.dto.auth.responses.PreRegisterResponse;
import dev.vaulton.vaultonapi.application.dto.auth.responses.RegisterResponse;
import dev.vaulton.vaultonapi.application.dto.shared.EncryptedValueDto;
import dev.vaulton.vaultonapi.application.usecase.auth.preregister.PreRegisterUseCase;
import dev.vaulton.vaultonapi.application.usecase.auth.register.RegisterUserUseCase;
import dev.vaulton.vaultonapi.domain.exception.VaultonDomainException;
import dev.vaulton.vaultonapi.infrastructure.config.SecurityConfig;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private PreRegisterUseCase preRegisterUseCase;

  @MockitoBean private RegisterUserUseCase registerUserUseCase;

  @Test
  void preRegister_ShouldReturnOk() throws Exception {
    UUID accountId = UUID.randomUUID();
    when(preRegisterUseCase.execute()).thenReturn(new PreRegisterResponse(accountId, 1));

    mockMvc
        .perform(post("/auth/pre-register"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.AccountId").value(accountId.toString()));
  }

  @Test
  void register_ShouldReturnCreated() throws Exception {
    UUID accountId = UUID.randomUUID();
    EncryptedValueDto dummyEncrypted =
        new EncryptedValueDto(new byte[12], new byte[16], new byte[16]);

    RegisterRequest request =
        new RegisterRequest(
            accountId,
            new byte[] {1, 2, 3}, // verifier
            new byte[] {4, 5, 6}, // adminVerifier
            new byte[] {7, 8, 9}, // rkVerifier
            new byte[] {10, 11, 12}, // s_pwd
            0, // kdfMode
            dummyEncrypted, // mkWrapPwd
            dummyEncrypted, // mkWrapRk
            1 // cryptoSchemaVer
            );

    when(registerUserUseCase.execute(any(RegisterRequest.class)))
        .thenReturn(new RegisterResponse(accountId));

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.AccountId").value(accountId.toString()));
  }

  @Test
  void register_ShouldReturnBadRequest_WhenAccountExists() throws Exception {
    UUID accountId = UUID.randomUUID();
    RegisterRequest request = createValidRequest(accountId);

    when(registerUserUseCase.execute(any(RegisterRequest.class)))
        .thenThrow(
            new VaultonDomainException(
                "Account already exists for ID: " + accountId, "Account cannot be created."));

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Account cannot be created."));
  }

  @Test
  void register_ShouldReturnBadRequest_WhenCryptoBlobInvalid() throws Exception {
    UUID accountId = UUID.randomUUID();
    RegisterRequest request = createValidRequest(accountId);

    when(registerUserUseCase.execute(any(RegisterRequest.class)))
        .thenThrow(new VaultonDomainException("Invalid crypto blob sizes."));

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid crypto blob sizes."));
  }

  @Test
  void register_ShouldReturnBadRequest_WhenMissingRequiredComponents() throws Exception {
    UUID accountId = UUID.randomUUID();
    String invalidJson =
        objectMapper
            .writeValueAsString(createValidRequest(accountId))
            .replace("\"MKWrapPwd\":{", "\"MKWrapPwd\":null,\"ignored\":{");

    when(registerUserUseCase.execute(any(RegisterRequest.class)))
        .thenThrow(
            new VaultonDomainException(
                "Malformed registration request: missing components",
                "Invalid crypto blob sizes."));

    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid crypto blob sizes."));
  }

  @Test
  void register_ShouldReturnBadRequest_WhenKdfModeValueIsUnsupported() throws Exception {
    UUID accountId = UUID.randomUUID();
    String invalidJson =
        objectMapper
            .writeValueAsString(createValidRequest(accountId))
            .replace("\"KdfMode\":0", "\"KdfMode\":99");

    when(registerUserUseCase.execute(any(RegisterRequest.class)))
        .thenThrow(new VaultonDomainException("Invalid KDF mode."));

    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid KDF mode."));
  }

  private RegisterRequest createValidRequest(UUID accountId) {
    EncryptedValueDto dummyEncrypted =
        new EncryptedValueDto(new byte[12], new byte[16], new byte[16]);

    return new RegisterRequest(
        accountId,
        new byte[] {1, 2, 3}, // verifier
        new byte[] {4, 5, 6}, // adminVerifier
        new byte[] {7, 8, 9}, // rkVerifier
        new byte[] {10, 11, 12}, // s_pwd
        0, // kdfMode
        dummyEncrypted, // mkWrapPwd
        dummyEncrypted, // mkWrapRk
        1 // cryptoSchemaVer
        );
  }
}
