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
import com.lilkhalil.listenloud.model.UserTag;
import com.lilkhalil.listenloud.model.UserTagKey;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.MusicTagRepository;
import com.lilkhalil.listenloud.repository.TagRepository;
import com.lilkhalil.listenloud.repository.UserTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    private final MusicRepository musicRepository;

    private final MusicTagRepository musicTagRepository;

    private final TagRepository tagRepository;

    private final UserTagRepository userTagRepository;

    public List<TagDTO> getTags() {
        return tagRepository.findAll().stream().map(tagMapper::toDto).toList();
    }

    public List<TagDTO> getTagsFromSong(Long id) {
        return musicTagRepository.findTagsByMusic(musicRepository.findById(id).orElse(null)).stream().map(tagMapper::toDto).toList();
    }

    public void deleteTagsFromMusic(Long id) {
        musicTagRepository.deleteByMusic(musicRepository.findById(id).orElse(null));
    }

    public List<TagDTO> getTagsByUser() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return userTagRepository.findByUser(user).stream().map(tagMapper::toDto).toList();
    }

    public List<TagDTO> updateUserTags(List<String> tagTypes) {

        List<UserTag> userTags = new ArrayList<>();

        List<Tag> tagsDTO = new ArrayList<>();

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!userTagRepository.findByUser(user).isEmpty())
            deleteUserTags(user);

        tagTypes.forEach((tagType) -> {
            Tag temp = tagRepository.findByName(TagType.valueOf(tagType)).orElse(null);

            tagsDTO.add(temp);
            
            userTags.add(
                new UserTag(
                    new UserTagKey(user.getId(), temp.getId()),
                    user,
                    temp
                )
            );
        });

        userTagRepository.saveAll(userTags);

        return tagsDTO.stream().map(tagMapper::toDto).toList();
    }

    private void deleteUserTags(User user) {
        userTagRepository.deleteTagsByUser(user);
    }

}
