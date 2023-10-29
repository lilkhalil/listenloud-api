package com.lilkhalil.listenloud.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lilkhalil.listenloud.service.AuthenticationService;
import com.lilkhalil.listenloud.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Класс-контроллер по обработке запросов от пользователя
 * на конечной точке <code>/api/v1/auth</code>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.service.AuthenticationService}
     */
    private final AuthenticationService authenticationService;
    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.service.LogoutService}
     */
    private final LogoutService logoutService;


    @PostMapping("/register")
    public ResponseEntity<?> register(
        @RequestParam String username,
        @RequestParam String password
    ) 
    {
        return ResponseEntity.ok(authenticationService.register(username, password));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
        @RequestParam String username,
        @RequestParam String password
    )
    {
        return ResponseEntity.ok(authenticationService.authenticate(username, password));
    }

    /**
     * Метод по обработке запроса на обновление токена доступа
     * 
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws Exception
     */
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        authenticationService.refreshToken(request, response);
    }

    /**
     * Метод по обработке запроса на деавторизацию
     * 
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws Exception
     */
    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logoutService.logout(request, response, null);
    }

}
