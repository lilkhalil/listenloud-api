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
public class SubscriptionKey implements Serializable {
    
    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Column(name = "publisher_id")
    private Long publisherId;

}
