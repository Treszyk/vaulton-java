package dev.vaulton.vaultonapi.application.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(@JsonProperty("message") String message) {}
