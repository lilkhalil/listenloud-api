package com.lilkhalil.listenloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.MusicTag;
import com.lilkhalil.listenloud.model.MusicTagKey;
import com.lilkhalil.listenloud.model.Tag;

import jakarta.transaction.Transactional;

public interface MusicTagRepository extends JpaRepository<MusicTag, MusicTagKey> {
    
    @Query("SELECT mt.tag FROM MusicTag mt WHERE mt.music = ?1")
    List<Tag> findTagsByMusic(Music music);

    @Query("SELECT m FROM MusicTag mt INNER JOIN Music m WHERE m = mt.music AND mt.tag IN ?1 GROUP BY m.id ORDER BY COUNT(m.id) DESC")
    List<Music> findMusicByTags(List<Tag> tags);

    @Transactional
    void deleteByMusic(Music music);

}
