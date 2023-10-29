package com.lilkhalil.listenloud.service;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilkhalil.listenloud.exception.UserAlreadyExistsException;
import com.lilkhalil.listenloud.model.AuthenticationResponse;
import com.lilkhalil.listenloud.model.Role;
import com.lilkhalil.listenloud.model.Token;
import com.lilkhalil.listenloud.model.TokenType;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.TokenRepository;
import com.lilkhalil.listenloud.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Сервис аутентификации. Указывает, что аннотированный класс — это «Сервис», первоначально
 * определенный в Domain-Driven Design (Evans, 2003) как «операция, предлагаемая
 * в качестве автономного интерфейса в модели без инкапсулированного состояния».
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.UserRepository}
     */
    private final UserRepository repository;

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.TokenRepository}
     */
    private final TokenRepository tokenRepository;

    /**
     * Экземпляр класса
     * {@link org.springframework.security.crypto.password.PasswordEncoder}
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.service.JwtService}
     */
    private final JwtService jwtService;

    /**
     * Экземпляр класса
     * {@link org.springframework.security.authentication.AuthenticationManager}
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Метод регистрации пользователя
     * 
     * @param request тело запроса
     *                {@link com.lilkhalil.listenloud.model.RegistrationRequest} на
     *                регистрацию пользователя
     * @return тело ответа
     *         {@link com.lilkhalil.listenloud.model.AuthenticationResponse} на
     *         регистрацию пользователя
     */
    public String register(
        String username,
        String password
    )
    {
        if (repository.findByUsername(username).orElse(null) != null)
            throw new UserAlreadyExistsException("User already exists! Please choose a different username!");

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();
        
        repository.save(user);
        
        return "Successfully created user!";
    }

    /**
     * Метод регистрации пользователя
     * 
     * @param request тело запроса
     *                {@link com.lilkhalil.listenloud.model.AuthenticationRequest}
     *                на аутентфикацию пользователя
     * @return тело ответа
     *         {@link com.lilkhalil.listenloud.model.AuthenticationResponse} на
     *         авторизацию пользователя
     */
    public AuthenticationResponse authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        User user = repository.findByUsername(username)
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Вспомогательный метод для сохранения токена пользователя
     * 
     * @param user     сущность пользователя в базе данных
     * @param jwtToken сущность токена в базе данных
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Вспомогательный метод по удалению всех токенов пользователя
     * 
     * @param user сущность пользователя, чьи токены удаляются
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Метод обновления токена доступа
     * 
     * @param request  HTTP запрос
     * @param response HTTP ответ
     * @throws IOException
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.repository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}