package com.lilkhalil.listenloud.controllers;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.MusicRequest;
import com.lilkhalil.listenloud.service.MusicService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
public class MusicController {
    
    private final MusicService musicService;

    @GetMapping
	public RepresentationModel<?> music() {
		RepresentationModel<?> music = new RepresentationModel<>();
		music.add(linkTo(methodOn(MusicController.class).add(null, null)).withRel("Добавление трека"));
		music.add(linkTo(methodOn(MusicController.class).getOne(null)).withRel("Получение трека"));
		music.add(linkTo(methodOn(MusicController.class).edit(null, null, null)).withRel("Изменение трека"));
		music.add(linkTo(methodOn(MusicController.class).delete(null)).withRel("Удаление трека"));
		return music;
	}

    @GetMapping("/all")
    public ResponseEntity<List<Music>> getAll() {
        return new ResponseEntity<>(musicService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Music> getOne(@PathVariable Long id) {
        return new ResponseEntity<>(musicService.getOne(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Music> add(
        @RequestBody MusicRequest musicRequest,
        HttpServletRequest request
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Music music = musicService.add(musicRequest, request);
        httpHeaders.add("Location", "/api/v1/music/" + music.getId().toString());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Music> edit(
        @PathVariable Long id,
        @RequestBody MusicRequest musicRequest,
        HttpServletRequest request
    ) {
        return new ResponseEntity<>(musicService.edit(id, musicRequest, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Music> delete(@PathVariable Long id) {
        musicService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
