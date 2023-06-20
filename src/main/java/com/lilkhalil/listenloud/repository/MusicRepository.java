package com.lilkhalil.listenloud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    @Query(value = "SELECT COUNT(*) FROM likes WHERE music_id = ?1", nativeQuery = true)
    Long findLikesCountByMusicId(Long id);

    @Query(value = "SELECT CASE WHEN EXISTS(SELECT * FROM likes l WHERE l.user_id = ?1 AND l.music_id = ?2) THEN true ELSE false END", nativeQuery = true)
    Boolean isLikedByUser(Long userId, Long musicId);

    @Query(value = "SELECT m.* FROM likes l INNER JOIN music m ON l.music_id = m.id GROUP BY m.id ORDER BY COUNT(m.id) DESC", nativeQuery = true)
    List<Music> findAllOrderByLikesCount();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM likes l WHERE l.music_id = ?1", nativeQuery = true)
    void deleteLikesByMusic(Long musicId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM likes l WHERE l.user_id = ?1 AND l.music_id = ?2", nativeQuery = true)
    void deleteLikeByUserAndMusic(Long userId, Long musicId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO likes (user_id, music_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveLikeByUserAndMusic(Long userId, Long musicId);

    @Query(value = "SELECT m.* FROM music_tags mt INNER JOIN music m ON mt.music_id = m.id WHERE mt.tag_id IN ?1 GROUP BY m.id ORDER BY COUNT(m.id) DESC", nativeQuery = true)
    List<Music> findMusicByTags(List<Long> ids);

    @Query(value = "SELECT m.* FROM saves s INNER JOIN music m ON m.id = s.music_id WHERE s.user_id = ?1", nativeQuery = true)
    List<Music> findSavedMusicByUser(Long id);

    @Query(value = "INSERT INTO saves VALUES (?1, ?2)", nativeQuery = true)
    @Transactional
    @Modifying
    void saveSavedMusicByUser(Long musicId, Long userId);

    @Query(value = "DELETE FROM saves s WHERE s.music_id = ?1 AND s.user_id = ?2", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteSaveByMusicAndUser(Long musicId, Long userId);

    @Query(value = "DELETE FROM saves s WHERE s.user_id = ?1", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteSavesByUser(Long id);

    @Query(value = "DELETE FROM saves s WHERE s.music_id = ?1", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteSavesByMusic(Long id);

}
