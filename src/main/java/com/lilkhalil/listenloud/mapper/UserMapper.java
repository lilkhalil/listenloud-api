package com.lilkhalil.listenloud.mapper;

import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.dto.UserDTO;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final TagMapper tagMapper;

    private final TagRepository tagRepository;

    public UserDTO toDto(User user) {
        Long id = user.getId();
        String username = user.getUsername();
        String biography = user.getBiography();
        String imageUrl = user.getImage();
        String roleName = user.getRole().name();
        return UserDTO.builder()
                .id(id)
                .username(username)
                .biography(biography)
                .imageUrl(imageUrl)
                .roleName(roleName)
                .tags(tagRepository.findTagsByUser(id).stream().map(tagMapper::toDto).toList())
                .build();
    }

}
