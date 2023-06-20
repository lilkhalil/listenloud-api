package com.lilkhalil.listenloud.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс-сущность для сохранения и прослушивания музыки в приложении
 * 
 * @author <strong>Aidar Khalilov</strong>
 */
@Getter
@Setter
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
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToMany(mappedBy = "savedSongs")
    private Set<User> saves;

    @ManyToMany(mappedBy = "likedSongs")
    private Set<User> likes;

    @ManyToMany
    @JoinTable(
        name = "music_tags",
        joinColumns = @JoinColumn(name = "music_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

}
