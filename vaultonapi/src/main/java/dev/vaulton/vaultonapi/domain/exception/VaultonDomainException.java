package dev.vaulton.vaultonapi.domain.exception;

import lombok.Getter;

@Getter
public class VaultonDomainException extends RuntimeException {
  private final String publicMessage;

  public VaultonDomainException(String internalMessage) {
    super(internalMessage);
    this.publicMessage = internalMessage;
  }

  public VaultonDomainException(String internalMessage, String publicMessage) {
    super(internalMessage);
    this.publicMessage = publicMessage;
  }
}
