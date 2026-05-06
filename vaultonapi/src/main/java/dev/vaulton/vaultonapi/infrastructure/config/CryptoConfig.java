package dev.vaulton.vaultonapi.infrastructure.config;

import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.shared.CryptoService;
import dev.vaulton.vaultonapi.domain.service.usercreation.UserCreationService;
import dev.vaulton.vaultonapi.domain.service.usercreation.UserCreationServiceImpl;
import dev.vaulton.vaultonapi.infrastructure.crypto.BouncyCryptoAdapter;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

  @Bean
  public CryptoService cryptoService(
      @Value("${vaulton.auth.fake-salt-secret}") String fakeSaltB64) {
    byte[] secret = Base64.getDecoder().decode(fakeSaltB64);

    return new BouncyCryptoAdapter(new SecureRandom(), secret);
  }

  @Bean
  public UserCreationService userCreationService(
      CryptoService cryptoService,
      UserRepository userRepository,
      @Value("${vaulton.auth.pepper}") String pepperB64,
      @Value("${vaulton.auth.iterations}") int iterations) {

    byte[] pepper = Base64.getDecoder().decode(pepperB64);

    return new UserCreationServiceImpl(iterations, pepper, cryptoService, userRepository);
  }
}
