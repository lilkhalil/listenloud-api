package com.lilkhalil.listenloud.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.lilkhalil.listenloud.exception.NotValidContentTypeException;
import com.lilkhalil.listenloud.service.MusicService;
import com.lilkhalil.listenloud.service.TagService;
import com.lilkhalil.listenloud.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final TagService tagService;

    private final UserService userService;

    private final MusicService musicService;

    @PostMapping("/tags")
    public ResponseEntity<?> updateUserTags(@RequestBody List<String> tagTypes) {
        return new ResponseEntity<>(tagService.updateUserTags(tagTypes), HttpStatus.OK);
    }
    
    @GetMapping("/tags")
    public ResponseEntity<?> readUserTags() {
        return new ResponseEntity<>(tagService.getTagsByUser(), HttpStatus.OK);
    }

    @GetMapping("/uploaded")
    public ResponseEntity<?> readUploadedSongs() {
        return new ResponseEntity<>(musicService.getUploadedSongs(), HttpStatus.OK);
    }

    @GetMapping("/saved")
    public ResponseEntity<?> readSavedSongs() {
        return new ResponseEntity<>(musicService.getSavedSongs(), HttpStatus.OK);
    }

    @DeleteMapping("/saved/{id}")
    public ResponseEntity<?> deleteSavedSong(@PathVariable Long id) {
        return new ResponseEntity<>(musicService.deleteSavedSong(id), HttpStatus.OK);
    }

    @DeleteMapping("/saved")
    public ResponseEntity<?> deleteSavedSongs() {
        musicService.deleteSavedSongs();
        return new ResponseEntity<>("All saved songs has been deleted!", HttpStatus.OK);
    }
 
    @GetMapping("/relevant")
    public ResponseEntity<?> readRelevantSongs() {
        return new ResponseEntity<>(musicService.getRelevantSongs(), HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<?> readSubscriptions() {
        return new ResponseEntity<>(userService.getSubscriptions(), HttpStatus.OK);
    }

    @GetMapping("/subscribers")
    public ResponseEntity<?> readSubscribers() {
        return new ResponseEntity<>(userService.getSubscribers(), HttpStatus.OK);
    }

    @PostMapping("/subscribe/{id}")
    public ResponseEntity<?> subscribe(@PathVariable Long id) {
        return new ResponseEntity<>(userService.subscribe(id), HttpStatus.OK);
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeAllById(@RequestBody List<Long> ids) {
        return new ResponseEntity<>(userService.unsubscribeAllById(ids), HttpStatus.OK);
    }

    @DeleteMapping("/unsubscribe/{id}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long id) {
        return new ResponseEntity<>(userService.unsubscribe(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> readUser() {
        return new ResponseEntity<>(userService.getUser(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
        @PathVariable Long id,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String biography,
        @RequestParam(required = false) MultipartFile image
    ) throws IOException, NotValidContentTypeException {
        return new ResponseEntity<>(userService.updateUser(id, username, biography, image), HttpStatus.OK);
    }
    
}
