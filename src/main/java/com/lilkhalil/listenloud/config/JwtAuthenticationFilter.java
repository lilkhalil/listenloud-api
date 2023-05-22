package com.lilkhalil.listenloud.config;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilkhalil.listenloud.model.ExceptionResponse;
import com.lilkhalil.listenloud.repository.TokenRepository;
import com.lilkhalil.listenloud.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 
 * Указывает, что аннотированный класс является "компонентом". Такие классы
 * рассматриваются как кандидаты на автоматическое обнаружение при использовании
 * конфигурации на основе аннотаций и сканирования путей к классам.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.service.JwtService}
     */
    private final JwtService jwtService;
    /**
     * Экземпляр класса
     * {@link org.springframework.security.core.userdetails.UserDetailsService}
     */
    private final UserDetailsService userDetailsService;
    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.TokenRepository}
     */
    private final TokenRepository tokenRepository;

    /**
     * Тот же контракт, что и для {@code doFilter}, но гарантированно
     * вызывается один раз для каждого запроса в одном потоке запросов.
     * See {@code shouldNotFilterAsyncDispatch} for details.
     * 
     * @param request     интерфейс для предоставления информации запроса для
     *                    сервлетов HTTP.
     * @param response    интерфейс для предоставления специфичных для HTTP функций
     *                    при отправке ответа
     * @param filterChain объект, предоставляемый контейнером сервлета разработчику,
     *                    дающий представление о цепочке вызовов отфильтрованного
     *                    запроса на ресурс.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userdetails = this.userDetailsService.loadUserByUsername(username);
                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);
                if (jwtService.isTokenValid(jwt, userdetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userdetails,
                            null,
                            userdetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(
                convertObjectToJson(ExceptionResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build()
                )
            );
            return;
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(
                convertObjectToJson(ExceptionResponse.builder()
                    .code(HttpStatus.NOT_FOUND.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build()
                )
            );
            return;
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
