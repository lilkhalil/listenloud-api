package com.lilkhalil.listenloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.Save;
import com.lilkhalil.listenloud.model.User;

import jakarta.transaction.Transactional;

public interface SaveRepository extends JpaRepository<Save, Long> {
    
    @Query("SELECT s.music FROM Save s WHERE s.user = ?1")
    List<Music> findByUser(User user);

    @Transactional
    void deleteByMusicAndUser(Music music, User user);

    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByMusic(Music music);

}
