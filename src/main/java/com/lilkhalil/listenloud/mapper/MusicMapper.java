package com.lilkhalil.listenloud.mapper;

import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.dto.MusicDTO;
import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MusicMapper {

    private final TagMapper tagMapper;

    private final UserMapper userMapper;

    private final MusicRepository musicRepository;

    private final TagRepository tagRepository;

    public MusicDTO toDto(Music music) {

        return MusicDTO.builder()
                .id(music.getId())
                .name(music.getName())
                .description(music.getDescription())
                .imageUrl(music.getImage())
                .audioUrl(music.getAudio())
                .author(userMapper.toDto(music.getAuthor()))
                .tags(tagRepository.findTagsByMusic(music.getId()).stream().map(tagMapper::toDto)
                        .collect(Collectors.toList()))
                .isLiked(musicRepository.isLikedByUser(
                        ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId(), music.getId()))
                .likesCount(musicRepository.findLikesCountByMusicId(music.getId()))
                .build();

    }

}
