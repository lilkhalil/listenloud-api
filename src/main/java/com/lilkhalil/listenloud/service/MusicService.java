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

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public List<Music> getAll() {
        return musicRepository.findAll();
    }

    public Music getOne(Long id) {
        return musicRepository.findById(id).orElse(null);
    }

    public Music add(
        @RequestBody MusicRequest musicRequest,
        HttpServletRequest request
    ) {
        var music = Music.builder()
            .name(musicRequest.getName())
            .description(musicRequest.getDescription())
            .image(getImage(musicRequest.getImage()))
            .audio(getAudio(musicRequest.getAudio()))
            .author(getUser(request))
            .build();
        return musicRepository.save(music);
    }

    public Music edit(
        Long id,
        @RequestBody MusicRequest musicRequest,
        HttpServletRequest request
    ) {
        Music music = musicRepository.getReferenceById(id);
        music.setName(musicRequest.getName() == null ? music.getName() : musicRequest.getName());
        music.setDescription(musicRequest.getDescription() == null ? music.getDescription() : musicRequest.getDescription());
        music.setImage(musicRequest.getImage() == null ? music.getImage() : musicRequest.getImage());
        music.setAudio(musicRequest.getAudio() == null ? music.getAudio() : musicRequest.getAudio());
        return musicRepository.save(music);
    }

    public void delete(
        Long id
    ) {
        musicRepository.deleteById(id);
        return;
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
        final String username = jwtService.extractUsername(authHeader.substring(7));
        return userRepository.findByUsername(username).orElse(null);
    }

}
