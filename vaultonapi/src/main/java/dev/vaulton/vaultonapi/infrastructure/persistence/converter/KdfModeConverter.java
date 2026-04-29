package dev.vaulton.vaultonapi.infrastructure.persistence.converter;

import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class KdfModeConverter implements AttributeConverter<KdfMode, Integer> {

  @Override
  public Integer convertToDatabaseColumn(KdfMode kdfMode) {
    if (kdfMode == null) {
      return null;
    }
    return kdfMode.getValue();
  }

  @Override
  public KdfMode convertToEntityAttribute(Integer code) {
    if (code == null) {
      return null;
    }

    return Stream.of(KdfMode.values())
        .filter(c -> c.getValue() == code)
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
