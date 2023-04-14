package com.lilkhalil.listenloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicRequest {
    
    private String name;

    private String audio;

    private String image;

    private String description;

}
