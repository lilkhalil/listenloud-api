package com.lilkhalil.listenloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lilkhalil.listenloud.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Класс-конфигуратор процесса аутентификации. Указывает, что класс объявляет
 * один или несколько методов @Bean и может обрабатываться контейнером Spring
 * для создания определений компонентов и запросов на обслуживание для этих
 * компонентов во время выполнения.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.UserRepository}
     */
    private final UserRepository userRepository;

    /**
     * Основной интерфейс, который загружает пользовательские данные.
     * Он используется во всей структуре как пользовательский DAO и является
     * стратегией, используемой DaoAuthenticationProvider.
     * 
     * Для интерфейса требуется только один метод только для чтения, что упрощает
     * поддержку новых стратегий доступа к данным.
     * 
     * @return полностью заполненная запись пользователя (никогда не нулевая)
     * @throws UsernameNotFoundException если пользователя не удалось найти или у
     *                                   пользователя нет GrantedAuthority
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Указывает, что класс может обрабатывать определенный
     * {@link org.springframework.security.core.Authentication} реализацию.
     * 
     * @return <code>true</code> если реализация может более точно оценить
     *         представленный класс Authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * Обрабатывает запрос {@link org.springframework.security.core.Authentication}
     * 
     * @param authenticationConfiguration Exports the authentication
     *                                    {@link Configuration}
     * @return полностью аутентифицированный объект, включая учетные данные
     * @throws Exception если аутентификация не удалась
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Сервисный интерфейс для кодирования паролей.
     * Предпочтительной реализацией является {@code BCryptPasswordEncoder}.
     * 
     * @return шифрование пароля
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
