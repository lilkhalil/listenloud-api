package com.lilkhalil.listenloud.service;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.MusicRequest;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Сервис по управлению музыкой. Указывает, что аннотированный класс — это «Сервис», первоначально
 * определенный в Domain-Driven Design (Evans, 2003) как «операция, предлагаемая
 * в качестве автономного интерфейса в модели без инкапсулированного состояния».
 */
@Service
@RequiredArgsConstructor
public class MusicService {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.MusicRepository}
     */
    private final MusicRepository musicRepository;

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.UserRepository}
     */
    private final UserRepository userRepository;

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.TokenRepository}
     */
    private final JwtService jwtService;

    /**
     * Метод по извлечению всех доступных музыкальных композиций
     * 
     * @return список экземпляров класса
     *         {@link com.lilkhalil.listenloud.model.Music}
     */
    public List<Music> getAll() {
        return musicRepository.findAll();
    }

    /**
     * Метод по получению музыкальной композиции по уникальному идентификатору
     * 
     * @param id Уникальный идентификатор музыки
     * @return экземпляр класса {@link com.lilkhalil.listenloud.model.Music}
     */
    public Music getOne(Long id) {
        return musicRepository.findById(id).orElse(null);
    }

    /**
     * Метод по добавлению музыкальной композиции в базу данных
     * 
     * @param musicRequest Тело запроса на добавление трека
     * @param request      Запрос в формате HTTP 1.1 для получения заголовка
     *                     авторизации
     * @return экземпляр класса {@link com.lilkhalil.listenloud.model.Music}
     */
    public Music add(
            @RequestBody MusicRequest musicRequest,
            HttpServletRequest request) {
        var music = Music.builder()
                .name(musicRequest.getName())
                .description(musicRequest.getDescription())
                .image(getImage(musicRequest.getImage()))
                .audio(getAudio(musicRequest.getAudio()))
                .author(getUser(request))
                .build();
        return musicRepository.save(music);
    }

    /**
     * Метод по изменению музыкальной композиции
     * 
     * @param id           Уникальный идентификатор музыки
     * @param musicRequest Тело запроса на изменение трека
     * @param request      Запрос в формате HTTP 1.1 для получения заголовка
     *                     авторизации
     * @return экземпляр класса {@link com.lilkhalil.listenloud.model.Music}
     */
    public Music edit(
            Long id,
            @RequestBody MusicRequest musicRequest,
            HttpServletRequest request) {
        Music music = musicRepository.getReferenceById(id);
        music.setName(musicRequest.getName() == null ? music.getName() : musicRequest.getName());
        music.setDescription(
                musicRequest.getDescription() == null ? music.getDescription() : musicRequest.getDescription());
        music.setImage(musicRequest.getImage() == null ? music.getImage() : musicRequest.getImage());
        music.setAudio(musicRequest.getAudio() == null ? music.getAudio() : musicRequest.getAudio());
        return musicRepository.save(music);
    }

    /**
     * Метод по удалению музыкальной композиции
     * 
     * @param id Уникальный идентификатор музыки
     */
    public void delete(
            Long id) {
        musicRepository.deleteById(id);
        return;
    }

    /**
     * Вспомогательный метод для определения изображения обложки
     * @param request запрос пользователя, содержащий ссылку на изображение
     * @return ссылка на новое или старое изображение обложки
     */
    private String getImage(String request) {
        final String defaultValue = """
                https://static.vecteezy.com/system/\s
                resources/previews/005/337/799/original/\s
                icon-image-not-found-free-vector.jpg""";
        return request == null ? defaultValue : request;
    }

    /**
     * Вспомогательный метод для определения аудиофайла композиции
     * @param request запрос пользователя, содержащий ссылку на аудиофайл
     * @return ссылка на новый или старый аудиофайл композиции
     */
    private String getAudio(String request) {
        final String defaultValue = "https://mp3uks.ru/mp3/files/kizaru-break-up-mp3.mp3";
        return request == null ? defaultValue : request;
    }

    /**
     * Вспомогательный метод для определения пользователя
     * @param request запрос пользователя, содержащий заголовок <code>Authorization</code> с токеном
     * @return пользователь, загрузивший трек на площадку
     */
    private User getUser(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String username = jwtService.extractUsername(authHeader.substring(7));
        return userRepository.findByUsername(username).orElse(null);
    }

}
