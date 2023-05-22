package com.lilkhalil.listenloud.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
/**
 * Класс-сущность пользователя
 * 
 * @author <strong>Aidar Khalilov</strong>
 */
public class User implements UserDetails {

    /**
     * Идентификатор пользователя, первичный ключ в базе данных
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя с уникальным значением, альтернативный ключ в базе данных
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Пароль пользователя, использующий систему шифрования
     * {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder }
     */
    @Column(nullable = false)
    private String password;

    /**
     * Описания профиля пользователя
     */
    private String biography;

    /**
     * Ссылка на изображение профиля
     * <a href='https://aws.amazon.com/ru/s3/'>системе хранения</a>
     */
    private String image;

    /**
     * {@link com.lilkhalil.listenloud.model.Role } пользователя, задающая
     * привилегии
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "author")
    @Singular
    private Set<Music> addedSongs;

    @OneToMany(mappedBy = "user")
    @Singular
    private Set<UserTag> savedTags;

    @OneToMany(mappedBy = "user")
    private Set<Save> savedSongs;
    
    @OneToMany(mappedBy = "user")
    @Singular
    private Set<Like> likedSongs;

    @OneToMany(mappedBy = "subscriber")
    @Singular
    private Set<Subscription> subscribers;

    @OneToMany(mappedBy = "publisher")
    @Singular
    private Set<Subscription> publishers;

    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER)
    private Set<Message> sentMessages;

    @OneToMany(mappedBy = "recipient", fetch = FetchType.EAGER)
    private Set<Message> receivedMessages;

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     * 
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Returns the password used to authenticate the user.
     * 
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot
     * be
     * authenticated.
     * 
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     *         <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     * 
     * @return <code>true</code> if the user is not locked, <code>false</code>
     *         otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     * 
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired),
     *         <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     * 
     * @return <code>true</code> if the user is enabled, <code>false</code>
     *         otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
