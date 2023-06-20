package com.lilkhalil.listenloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.TagType;

import jakarta.transaction.Transactional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(TagType name);

    List<Tag> findAllByNameIn(List<TagType> name);

    @Query(value = "SELECT t.* FROM user_tags ut INNER JOIN tags t ON ut.tag_id = t.id WHERE ut.user_id = ?1", nativeQuery = true)
    List<Tag> findTagsByUser(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_tags ut WHERE ut.user_id = ?1", nativeQuery = true)
    void deleteTagsByUser(Long id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_tags (user_id, tag_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveTagByUserAndTag(Long userId, Long tagId);

    @Query(value = "SELECT t.* FROM music_tags mt INNER JOIN tags t ON mt.tag_id = t.id WHERE mt.music_id = ?1", nativeQuery = true)
    List<Tag> findTagsByMusic(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM music_tags mt WHERE mt.music_id = ?1", nativeQuery = true)
    void deleteTagsByMusic(Long id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO music_tags (music_id, tag_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveTagByMusicAndTag(Long musicId, Long tagId);

}
