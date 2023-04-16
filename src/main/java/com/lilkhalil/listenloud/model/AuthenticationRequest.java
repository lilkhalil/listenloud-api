package com.lilkhalil.listenloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-помощник для составляения тела запроса на аутентификацию пользователя
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Пароль
     */
    String password;

}
