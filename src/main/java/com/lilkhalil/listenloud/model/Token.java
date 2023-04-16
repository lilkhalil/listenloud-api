package com.lilkhalil.listenloud.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-сущносто токена.
 * JSON объект, который определен в открытом стандарте
 * <a href='https://www.rfc-editor.org/rfc/rfc7519'>RFC 7519</a>
 * JWT — это лишь строка в следующем формате header.payload.signature
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    /**
     * Идентификатор токена, первичный ключ в базе данных
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Значение токена, альтернативный ключ в базе данных
     */
    @Column(unique = true)
    private String token;

    /**
     * {@link com.lilkhalil.listenloud.model.TokenType} тип токена
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    /**
     * Состояние аннулированности токена
     */
    public boolean revoked;

    /**
     * Состояние истекания токена
     */
    public boolean expired;

    /**
     * Внешний ключ, ссылка на автора токена
     * {@link com.lilkhalil.listenloud.model.User}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

}
