package com.lilkhalil.listenloud.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lilkhalil.listenloud.dto.MusicDTO;
import com.lilkhalil.listenloud.exception.NotValidContentTypeException;
import com.lilkhalil.listenloud.mapper.MusicMapper;
import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.TagType;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.TagRepository;
import com.lilkhalil.listenloud.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Сервис по управлению музыкой. Указывает, что аннотированный класс — это «Сервис», первоначально
 * определенный в Domain-Driven Design (Evans, 2003) как «операция, предлагаемая
 * в качестве автономного интерфейса в модели без инкапсулированного состояния».
 */
@Service
@RequiredArgsConstructor
public class MusicService {

    /**
     * Экземпляр класса {@link com.lilkhalil.listenloud.repository.MusicRepository}
     */
    private final MusicRepository musicRepository;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    /** 
     * Экземпляр класса {@link com.lilkhalil.listenloud.service.StorageService}
    */
    private final StorageService storageService;

    private final MusicMapper musicMapper;
    /**
     * Метод по извлечению всех доступных музыкальных композиций
     * 
     * @return список экземпляров класса
     *         {@link com.lilkhalil.listenloud.model.Music}
     */
    public List<MusicDTO> getSongs() {
        return musicRepository.findAll().stream().map(musicMapper::toDto).toList();
    }

    public List<MusicDTO> getSongsByTags(List<String> tagTypes) {

        List<Tag> tags = tagRepository.findAllByNameIn(
            tagTypes.stream()
                .map(tag -> TagType.valueOf(tag)).toList()
        );

        return musicRepository.findMusicByTags(
            tags.stream().map(tag -> tag.getId()).toList()
        )
            .stream()
            .map(musicMapper::toDto)
            .toList();
    }

    public List<MusicDTO> getRelevantSongs() {
        
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Tag> tags = tagRepository.findTagsByUser(user.getId());

        if (tags.isEmpty()) {
            return Stream.concat(musicRepository.findAllOrderByLikesCount().stream(), musicRepository.findAll().stream())
                .distinct()
                .map(musicMapper::toDto)
                .toList();
        }

        return musicRepository.findMusicByTags(
            tags.stream().map(tag -> tag.getId()).toList()
        )
            .stream()
            .map(musicMapper::toDto)
            .toList();
    }

    public List<MusicDTO> getSongsBySubscriptions() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findSubscriptionsByUser(user.getId())
            .stream()
            .flatMap(publisher -> musicRepository.findByAuthor(publisher).stream())
            .map(musicMapper::toDto)
            .toList();
    }

    public List<MusicDTO> getUploadedSongs() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return musicRepository.findByAuthor(user).stream().map(musicMapper::toDto).toList();

    }

    public List<MusicDTO> getSavedSongs() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return musicRepository.findSavedMusicByUser(user.getId()).stream().map(musicMapper::toDto).toList();
    }

    /**
     * Метод по получению музыкальной композиции по уникальному идентификатору
     * 
     * @param id Уникальный идентификатор музыки
     * @return экземпляр класса {@link com.lilkhalil.listenloud.model.Music}
     */
    public MusicDTO getSongById(Long id) throws EntityNotFoundException
    {
        Music music = musicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return musicMapper.toDto(music);
    }

    public MusicDTO addSong(String name, String description, MultipartFile image, MultipartFile audio, List<String> tags) throws IOException, NotValidContentTypeException
    {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = Music.builder()
            .name(name)
            .description(description)
            .author(user)
            .build();

        storageService.isValidMediaType(audio);

        if (image == null) 
            music.setImage("https://listenloudstorage.storage.yandexcloud.net/NOT_FOUND.jpg");
        else {
            storageService.isValidMediaType(image);
            music.setImage(storageService.upload(image));
        }

        music.setAudio(storageService.upload(audio));

        musicRepository.save(music);

        if (tags != null) {
            tags.forEach(tagType -> {
                Tag tag = tagRepository.findByName(TagType.valueOf(tagType)).orElse(null);
                tagRepository.saveTagByMusicAndTag(music.getId(), tag.getId());
            });
        }

        return musicMapper.toDto(music);
    }

    public MusicDTO saveSong(Long id) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicRepository.findById(id).orElse(null);

        musicRepository.saveSavedMusicByUser(music.getId(), user.getId());

        return musicMapper.toDto(music);

    }

    public MusicDTO editSong(Long id, String name, String description, MultipartFile image, MultipartFile audio, List<String> tags) throws IOException, NotValidContentTypeException
    {
        Music music = musicRepository.findById(id).orElse(null);

        music.setName(name == null ? music.getName() : name);
        music.setDescription(description == null ? music.getDescription() : description);

        if (tags != null) {
            tags.forEach(tagType -> {
                Tag tag = tagRepository.findByName(TagType.valueOf(tagType)).orElse(null);
                tagRepository.saveTagByMusicAndTag(music.getId(), tag.getId());
            });
        }

        if (image == null && audio == null) {
            music.setImage(music.getImage());
            music.setAudio(music.getAudio());
        } 
        else if (image == null && audio != null) {
            storageService.isValidMediaType(audio);
            storageService.delete(music.getAudio());
            music.setImage(music.getImage());
            music.setAudio(storageService.upload(audio));
        } 
        else if (image != null && audio == null) {
            storageService.isValidMediaType(image);
            storageService.delete(music.getImage());
            music.setImage(storageService.upload(image));
            music.setAudio(music.getAudio());
        } 
        else {
            storageService.isValidMediaType(audio);
            storageService.isValidMediaType(image);
            storageService.delete(music.getAudio());
            storageService.delete(music.getImage());
            music.setImage(storageService.upload(image));
            music.setAudio(storageService.upload(audio));
        }

        return musicMapper.toDto(musicRepository.save(music));
    }

    /**
     * Метод по удалению музыкальной композиции
     * 
     * @param id Уникальный идентификатор музыки
     */
    public MusicDTO deleteSong(Long id) throws IOException
    {
        Music music = musicRepository.findById(id).orElse(null);

        storageService.delete(music.getAudio());
        storageService.delete(music.getImage());

        musicRepository.deleteLikesByMusic(music.getId());

        tagRepository.deleteTagsByMusic(music.getId());

        musicRepository.deleteSavesByMusic(music.getId());

        musicRepository.deleteById(id);

        return musicMapper.toDto(music);
    }

    public void deleteSongs() throws IOException {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Music> music = musicRepository.findByAuthor(user);

        music.forEach(song -> {
            storageService.delete(song.getAudio());
            storageService.delete(song.getImage());
            musicRepository.deleteLikesByMusic(song.getId());
            tagRepository.deleteTagsByMusic(song.getId());
            musicRepository.deleteSavesByMusic(song.getId());
        });

        musicRepository.deleteAll(music);

    }

    public MusicDTO deleteSavedSong(Long id) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicRepository.findById(id).orElse(null);

        musicRepository.deleteSaveByMusicAndUser(music.getId(), user.getId());

        return musicMapper.toDto(music);
    }

    public void deleteSavedSongs() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        musicRepository.deleteSavesByUser(user.getId());

    }

    public void rateMusic(Long id) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicRepository.findById(id).orElse(null);

        if (musicRepository.isLikedByUser(user.getId(), music.getId())) 
            musicRepository.deleteLikeByUserAndMusic(user.getId(), music.getId());
        else 
            musicRepository.saveLikeByUserAndMusic(user.getId(), music.getId());

    }

}
