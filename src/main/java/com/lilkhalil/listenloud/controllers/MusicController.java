package com.lilkhalil.listenloud.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lilkhalil.listenloud.dto.MusicDTO;
import com.lilkhalil.listenloud.exception.NotValidContentTypeException;
import com.lilkhalil.listenloud.model.ExceptionResponse;
import com.lilkhalil.listenloud.service.MusicService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Класс-контроллер по обработке запросов от пользователя
 * на конечной точке <code>/api/v1/music</code>
 */
@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
public class MusicController {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.service.MusicService}
     */
    private final MusicService musicService;

    @PostMapping
    public ResponseEntity<?> create(
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) MultipartFile image,
        @RequestParam MultipartFile audio,
        @RequestParam(name = "tags", required = false) List<String> tags
    ) {
        try {
            MusicDTO musicDTO = musicService.addSong(name, description, image, audio, tags);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Location", "/api/v1/music/" + musicDTO.getId().toString());
            return new ResponseEntity<>(musicDTO, headers, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.BAD_GATEWAY.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.BAD_GATEWAY);
        } catch (NotValidContentTypeException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    /**
     * Метод по обработке запроса на получение всех музыкальных композиций
     * 
     * @return JSON-представление списка экземпляров класса
     *         {@link com.lilkhalil.listenloud.model.Music}
     */
    @GetMapping
    public ResponseEntity<?> readSongs() {
        return new ResponseEntity<>(musicService.getSongs(), HttpStatus.OK);
    }

    /**
     * Метод по обработке запроса на получение музыкальной композиции по
     * идентификатору
     * 
     * @param id уникальный идентификатор композиции
     * @return JSON-представление списка экземпляров класса
     *         {@link com.lilkhalil.listenloud.model.Music}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        try {
            return new ResponseEntity<MusicDTO>(musicService.getSongById(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.NOT_FOUND.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @PathVariable Long id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) MultipartFile image,
        @RequestParam(required = false) MultipartFile audio,
        @RequestParam(name = "tags", required = false) List<String> tags
    ) {
        try {
            return new ResponseEntity<>(musicService.editSong(id, name, description, image, audio, tags), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.BAD_GATEWAY.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.BAD_GATEWAY);
        } catch (NotValidContentTypeException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(musicService.deleteSong(id), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.BAD_GATEWAY.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        try {
            musicService.deleteSongs();
            return new ResponseEntity<>("All uploaded songs has been deleted!", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.BAD_GATEWAY.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<?> rate(@PathVariable Long id) {
        musicService.rateMusic(id);
        return new ResponseEntity<>("Assesment has been provided!", HttpStatus.OK);
    }

    @PostMapping("/find")
    public ResponseEntity<?> readByTags(@RequestBody List<String> tagTypes) {
        return new ResponseEntity<>(musicService.getSongsByTags(tagTypes), HttpStatus.OK);
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<?> readByPublishers() {
        return new ResponseEntity<>(musicService.getSongsBySubscriptions(), HttpStatus.OK);
    }

    @PostMapping("{id}/save")
    public ResponseEntity<?> save(@PathVariable Long id) {
        return new ResponseEntity<>(musicService.saveSong(id), HttpStatus.OK);
    }

}
