package com.lilkhalil.listenloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.TagType;

public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(TagType name);

    List<Tag> findAllByNameIn(List<TagType> name);

}
