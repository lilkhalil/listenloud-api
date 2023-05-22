package com.lilkhalil.listenloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.model.Like;
import com.lilkhalil.listenloud.model.LikeKey;

import jakarta.transaction.Transactional;

public interface LikeRepository extends JpaRepository<Like, LikeKey>{

    @Query("SELECT CASE WHEN EXISTS(SELECT ul FROM Like ul WHERE ul.user = ?1 AND ul.music = ?2) THEN true ELSE false END")
    Boolean isLikedByUser(User user, Music music);

    @Query("SELECT COUNT(ul) FROM Like ul WHERE ul.music = ?1")
    Long likesCount(Music music);

    @Query("SELECT m FROM Like l INNER JOIN Music m WHERE m = l.music GROUP BY m.id ORDER BY COUNT(m.id) DESC")
    List<Music> findAllOrderByLikesCount();

    @Transactional
    void deleteByMusic(Music music);

}
