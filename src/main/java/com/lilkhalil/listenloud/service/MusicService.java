package com.lilkhalil.listenloud.service;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.MusicRequest;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ResponseEntity<List<Music>> getAll() {
        return ResponseEntity.ok(musicRepository.findAll());
    }

    public ResponseEntity<Music> add(
        @RequestBody MusicRequest musicRequest,
        HttpServletRequest request
    ) {
        var music = Music.builder()
            .name(musicRequest.getName())
            .image(getImage(musicRequest.getImage()))
            .audio(getAudio(musicRequest.getAudio()))
            .author(getUser(request))
            .build();
        musicRepository.save(music);
        return ResponseEntity.ok(music);
    }
    
    private String getImage(String request) {
        final String defaultValue = """
            https://static.vecteezy.com/system/\s
            resources/previews/005/337/799/original/\s
            icon-image-not-found-free-vector.jpg""";
        return request == null ? defaultValue : request;
    }

    private String getAudio(String request) {
        final String defaultValue = "https://mp3uks.ru/mp3/files/kizaru-break-up-mp3.mp3";
        return request == null ? defaultValue : request;
    }

    private User getUser(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt = jwtService.extractUsername(authHeader.substring(7));
        return userRepository.findByUsername(jwtService.extractUsername(jwt)).orElse(null);
    }

}
