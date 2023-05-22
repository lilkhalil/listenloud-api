package com.lilkhalil.listenloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.model.UserTag;
import com.lilkhalil.listenloud.model.UserTagKey;

import jakarta.transaction.Transactional;

public interface UserTagRepository extends JpaRepository<UserTag, UserTagKey> {
    
    @Query("SELECT ut.tag FROM UserTag ut WHERE ut.user = ?1")
    List<Tag> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserTag ut WHERE ut.user = ?1")
    void deleteTagsByUser(User user);
    
}
