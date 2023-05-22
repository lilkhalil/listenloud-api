package com.lilkhalil.listenloud.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MusicTagKey implements Serializable {
    
    @Column(name = "music_id")
    private Long musicId;

    @Column(name = "tag_id")
    private Long tagId;

}
