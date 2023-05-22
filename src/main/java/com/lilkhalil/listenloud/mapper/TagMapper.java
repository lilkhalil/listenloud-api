package com.lilkhalil.listenloud.mapper;

import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.dto.TagDTO;
import com.lilkhalil.listenloud.model.Tag;

@Component
public class TagMapper {
    
    public TagDTO toDto(Tag tag) {
        return TagDTO.builder()
                    .id(tag.getId())
                    .name(tag.getName().name())
                    .build();
    }

}
