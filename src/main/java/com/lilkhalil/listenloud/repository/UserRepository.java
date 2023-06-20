package com.lilkhalil.listenloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.User;

import jakarta.transaction.Transactional;

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

    @Query(value = "SELECT u.* FROM subscriptions s INNER JOIN users u ON u.id = s.subscriber_id WHERE s.publisher_id = ?1", nativeQuery = true)
    List<User> findSubscribersByUser(Long id);

    @Query(value = "SELECT u.* FROM subscriptions s INNER JOIN users u ON u.id = s.publisher_id WHERE s.subscriber_id = ?1", nativeQuery = true)
    List<User> findSubscriptionsByUser(Long id);

    @Query(value = "INSERT INTO subscriptions VALUES (?1, ?2)", nativeQuery = true)
    @Transactional
    @Modifying
    void saveSubscription(Long publisherId, Long subscriberId);

    @Query(value = "DELETE FROM subscriptions s WHERE s.subscriber_id = ?1 AND s.publisher_id = ?2", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteBySubscriberAndPublisher(Long subsriberId, Long publisherId);

    @Query(value = "DELETE FROM subscriptions s WHERE s.subscriber_id = ?1 AND s.publisher_id IN ?2", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteBySubscriberAndPublisherIn(Long subscriberId, List<Long> publisherIds);

}
