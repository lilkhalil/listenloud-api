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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;

    @GetMapping
	public RepresentationModel<?> authentication() {
		RepresentationModel<?> authentication = new RepresentationModel<>();
		authentication.add(linkTo(methodOn(AuthenticationController.class).register(null)).withRel("Регистрация"));
		authentication.add(linkTo(methodOn(AuthenticationController.class).authenticate(null)).withRel("Аутентификация"));
		return authentication;
	}

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(authenticationService.register(registrationRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate (
        @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/refresh-token")
    public void refreshToken (
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public void logout (
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        logoutService.logout(request, response, null);
    }

}
