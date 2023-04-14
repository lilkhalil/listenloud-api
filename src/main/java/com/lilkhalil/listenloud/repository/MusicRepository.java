package com.lilkhalil.listenloud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lilkhalil.listenloud.model.Music;

public interface MusicRepository extends JpaRepository<Music, Long> {
    
    Optional<Music> findByName(String name);

}
