package com.lilkhalil.listenloud.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/all")
    public ResponseEntity<List<Music>> getAll() {
        return musicService.getAll();
    }

    @PostMapping("/add")
    public ResponseEntity<Music> add(
        @RequestBody MusicRequest musicRequest,
        HttpServletRequest request
    ) {
        return musicService.add(musicRequest, request);
    }

}
