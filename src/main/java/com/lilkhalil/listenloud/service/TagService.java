package com.lilkhalil.listenloud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lilkhalil.listenloud.dto.TagDTO;
import com.lilkhalil.listenloud.mapper.TagMapper;
import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.TagType;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    private final MusicRepository musicRepository;

    private final TagRepository tagRepository;

    public List<TagDTO> getTags() {
        return tagRepository.findAll().stream().map(tagMapper::toDto).toList();
    }

    public List<TagDTO> getTagsFromSong(Long id) {
        return tagRepository.findTagsByMusic(musicRepository.findById(id).orElse(null).getId()).stream().map(tagMapper::toDto).toList();
    }

    public void deleteTagsFromMusic(Long id) {
        tagRepository.deleteTagsByMusic(musicRepository.findById(id).orElse(null).getId());
    }

    public List<TagDTO> getTagsByUser() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return tagRepository.findTagsByUser(user.getId()).stream().map(tagMapper::toDto).toList();
    }

    public List<TagDTO> updateUserTags(List<String> tagTypes) {

        List<Tag> tags = new ArrayList<>();

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!tagRepository.findTagsByUser(user.getId()).isEmpty())
            tagRepository.deleteTagsByUser(user.getId());

        tagTypes.forEach((tagType) -> {
            Tag tag = tagRepository.findByName(TagType.valueOf(tagType)).orElse(null);
            tags.add(tag);
            tagRepository.saveTagByUserAndTag(user.getId(), tag.getId());
        });

        return tags.stream().map(tagMapper::toDto).toList();
    }

}
