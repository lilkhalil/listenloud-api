package com.lilkhalil.listenloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-помощник для составления тела запроса на добавление трека
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicRequest {

    /**
     * Название трека
     */
    private String name;

    /**
     * Ссылка на аудиофайл
     */
    private String audio;

    /**
     * Ссылка на обложку
     */
    private String image;

    /**
     * Описание трека
     */
    private String description;

}
