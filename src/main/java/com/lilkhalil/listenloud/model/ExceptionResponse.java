package com.lilkhalil.listenloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    @JsonProperty("status")
    private final String status = "error";

    @JsonProperty("code")
    private String code;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("message")
    private String message;
}
