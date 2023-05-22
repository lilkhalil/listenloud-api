package com.lilkhalil.listenloud.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lilkhalil.listenloud.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<?> readTags() {
        return new ResponseEntity<>(tagService.getTags(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readTagsFromSong(@PathVariable Long id) {
        return new ResponseEntity<>(tagService.getTagsFromSong(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTagsFromMusic(@PathVariable Long id) {
        tagService.deleteTagsFromMusic(id);
        return new ResponseEntity<>("Success: Tags has been removed!", HttpStatus.OK);
    }

}
