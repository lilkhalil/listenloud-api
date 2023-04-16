package com.lilkhalil.listenloud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lilkhalil.listenloud.model.User;

/**
 * Указывает, что аннотированный класс представляет собой «репозиторий»,
 * первоначально определенный в Domain-Driven Design (Evans, 2003) как «механизм
 * для инкапсуляции поведения хранения, извлечения и поиска, который эмулирует
 * набор объектов». Средство взаимодействия с таблицей пользователей.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Имена производных методов состоят из двух основных частей, разделенных первым
     * ключевым словом By. Поиск по имени пользователя.
     * 
     * @param username имя пользователя
     * @return экземпляр класса {@link com.lilkhalil.listenloud.model.User}, или <code>null</code> в случае отсутствия пользователя
     *         с данным именем в базе данных
     */
    Optional<User> findByUsername(String username);
}
