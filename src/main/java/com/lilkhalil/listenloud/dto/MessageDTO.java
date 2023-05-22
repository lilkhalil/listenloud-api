package com.lilkhalil.listenloud.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO recipient;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isReaded;
}
