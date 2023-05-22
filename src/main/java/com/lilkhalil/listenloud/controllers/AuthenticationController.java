package com.lilkhalil.listenloud.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lilkhalil.listenloud.exception.NotValidContentTypeException;
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
        authentication.add(linkTo(methodOn(AuthenticationController.class).register(null, null, null)).withRel("Регистрация"));
        authentication
                .add(linkTo(methodOn(AuthenticationController.class).authenticate(null, null)).withRel("Аутентификация"));
        return authentication;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam(required = false) MultipartFile image
    ) 
    {
        try {
            return ResponseEntity.ok(authenticationService.register(username, password, image));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        } catch (NotValidContentTypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
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
