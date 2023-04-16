package com.lilkhalil.listenloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

/**
 * Класс-конфигуратор процесса аутентификации. Указывает, что класс объявляет
 * один или несколько методов @Bean и может обрабатываться контейнером Spring
 * для создания определений компонентов и запросов на обслуживание для этих
 * компонентов во время выполнения.
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    /**
     * Экземпляр класса
     * {@link com.lilkhalil.listenloud.config.JwtAuthenticationFilter}
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /**
     * Экземпляр класса
     * {@link org.springframework.security.authentication.AuthenticationProvider}
     */
    private final AuthenticationProvider authenticationProvider;
    /**
     * Экземпляр класса
     * {@link org.springframework.security.web.authentication.logout.LogoutHandler}
     */
    private final LogoutHandler logoutHandler;

    /**
     * Определяет цепочку фильтров, которую можно сопоставить с
     * {@code HttpServletRequest}
     * для того, чтобы решить, применимо ли оно к этому запросу.
     * 
     * @param http похож на XML &lt;http&gt; Spring Security элемент в
     *             конфигурация пространства имен. Это позволяет настроить
     *             веб-безопасность для определенных http запросов.
     * @return (Non-Javadoc)
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> {
                    logout.logoutUrl("/api/v1/auth/logout");
                    logout.addLogoutHandler(logoutHandler);
                    logout.logoutSuccessHandler(
                            (request, response, authentcaiton) -> SecurityContextHolder.clearContext());
                });

        return http.build();
    }
}
