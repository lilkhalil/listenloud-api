package com.lilkhalil.listenloud.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String audioUrl;
    private UserDTO author;
    private List<TagDTO> tags;
    private Long likesCount;
    private Boolean isLiked;
}
