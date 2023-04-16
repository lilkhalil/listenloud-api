package com.lilkhalil.listenloud.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lilkhalil.listenloud.model.AuthenticationRequest;
import com.lilkhalil.listenloud.model.AuthenticationResponse;
import com.lilkhalil.listenloud.model.RegistrationRequest;
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

    /**
     * Метод определения доступных конечных точек в виде <a href=
     * 'https://ru.wikipedia.org/wiki/Hypertext_Application_Language'>HAL</a>
     * 
     * @return представление конечной точки <code>/api/v1/auth</code>
     */
    @GetMapping
    public RepresentationModel<?> authentication() {
        RepresentationModel<?> authentication = new RepresentationModel<>();
        authentication.add(linkTo(methodOn(AuthenticationController.class).register(null)).withRel("Регистрация"));
        authentication
                .add(linkTo(methodOn(AuthenticationController.class).authenticate(null)).withRel("Аутентификация"));
        return authentication;
    }

    /**
     * Метод по обработке запроса на регистрацию
     * 
     * @param registrationRequest запрос пользователя на регистрацию
     * @return JSON-представление тела ответа
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(authenticationService.register(registrationRequest));
    }

    /**
     * Метод по обработке запроса на аутентификацию
     * 
     * @param authenticationRequest Запрос пользователя на аутентификацию
     * @return JSON-представление тела ответа
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
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
