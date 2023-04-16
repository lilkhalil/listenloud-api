package com.lilkhalil.listenloud.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-сущность для сохранения и прослушивания музыки в приложении
 * 
 * @author <strong>Aidar Khalilov</strong>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "music")
public class Music {

    /**
     * Идентификатор музыки, первичный ключ в базе данных
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Наименование трека в базе данных
     */
    @Column(nullable = false)
    private String name;

    /**
     * Описание музыкального трека
     */
    private String description;

    /**
     * Ссылка на изображение обложки в
     * <a href='https://aws.amazon.com/ru/s3/'>системе хранения</a>
     */
    private String image;

    /**
     * Ссылка на аудиофайл в
     * <a href='https://aws.amazon.com/ru/s3/'>системе хранения</a>
     */
    @Column(nullable = false)
    private String audio;

    /**
     * Внешний ключ, ссылка на автора музыки
     * {@link com.lilkhalil.listenloud.model.User }
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

}
