package com.lilkhalil.listenloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-помощник для составления тела ответа на регистрацию / авторизацию
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    /**
     * Токен доступа с помощью которого можно получить доступ к защищенным ресурсам
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Рефреш-токен выполняют только одну специфичную задачу — получение нового токена доступа
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

}
