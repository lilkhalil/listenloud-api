package com.lilkhalil.listenloud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.User;

import jakarta.transaction.Transactional;

import java.util.List;


/**
 * Указывает, что аннотированный класс представляет собой «репозиторий»,
 * первоначально определенный в Domain-Driven Design (Evans, 2003) как «механизм
 * для инкапсуляции поведения хранения, извлечения и поиска, который эмулирует
 * набор объектов». Средство взаимодействия с таблицей музыки.
 */
public interface MusicRepository extends JpaRepository<Music, Long> {
    /**
     * Имена производных методов состоят из двух основных частей, разделенных первым ключевым словом By.
     * @param name название композиции
     * @return экземпляр класса {@link com.lilkhalil.listenloud.model.Music} или <code>null</code> в случае отсутствия в базе данных.
     */
    Optional<Music> findByName(String name);

    List<Music> findByAuthor(User author);

    @Transactional
    void deleteByAuthor(User user);

}
