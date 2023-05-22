package com.lilkhalil.listenloud.mapper;

import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.dto.MusicDTO;
import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MusicTagRepository;
import com.lilkhalil.listenloud.repository.LikeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MusicMapper {

    private final TagMapper tagMapper;

    private final UserMapper userMapper;

    private final MusicTagRepository musicTagRepository;

    private final LikeRepository userLikesRepository;

    public MusicDTO toDto(Music music) {

        return MusicDTO.builder()
                .id(music.getId())
                .name(music.getName())
                .description(music.getDescription())
                .imageUrl(music.getImage())
                .audioUrl(music.getAudio())
                .author(userMapper.toDto(music.getAuthor()))
                .tags(musicTagRepository.findTagsByMusic(music).stream().map(tagMapper::toDto)
                        .collect(Collectors.toList()))
                .isLiked(userLikesRepository.isLikedByUser(
                        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), music))
                .likesCount(userLikesRepository.likesCount(music))
                .build();

    }

}
