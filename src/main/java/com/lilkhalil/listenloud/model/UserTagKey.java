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
public class UserTagKey implements Serializable {
    
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tag_id")
    private Long tagId;

}
