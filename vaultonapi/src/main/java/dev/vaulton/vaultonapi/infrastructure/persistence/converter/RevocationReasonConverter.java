package dev.vaulton.vaultonapi.infrastructure.persistence.converter;

import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RevocationReasonConverter implements AttributeConverter<RevocationReason, Integer> {

  @Override
  public Integer convertToDatabaseColumn(RevocationReason reason) {
    if (reason == null) {
      return null;
    }
    return reason.getValue();
  }

  @Override
  public RevocationReason convertToEntityAttribute(Integer code) {
    if (code == null) {
      return null;
    }

    return Stream.of(RevocationReason.values())
        .filter(c -> c.getValue() == code)
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
