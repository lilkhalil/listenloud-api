package com.lilkhalil.listenloud.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.dto.UserDTO;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.UserTagRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final TagMapper tagMapper;

    private final UserTagRepository userTagRepository;

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
                .tags(userTagRepository.findByUser(user).stream().map(tagMapper::toDto).collect(Collectors.toList()))
                .build();
    }

}
