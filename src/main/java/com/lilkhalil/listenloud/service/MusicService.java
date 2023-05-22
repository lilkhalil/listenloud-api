package com.lilkhalil.listenloud.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lilkhalil.listenloud.dto.MusicDTO;
import com.lilkhalil.listenloud.exception.NotValidContentTypeException;
import com.lilkhalil.listenloud.mapper.MusicMapper;
import com.lilkhalil.listenloud.model.Music;
import com.lilkhalil.listenloud.model.MusicTag;
import com.lilkhalil.listenloud.model.MusicTagKey;
import com.lilkhalil.listenloud.model.Save;
import com.lilkhalil.listenloud.model.SaveKey;
import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.TagType;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.model.Like;
import com.lilkhalil.listenloud.model.LikeKey;
import com.lilkhalil.listenloud.repository.MusicRepository;
import com.lilkhalil.listenloud.repository.MusicTagRepository;
import com.lilkhalil.listenloud.repository.SaveRepository;
import com.lilkhalil.listenloud.repository.SubscriptionRepository;
import com.lilkhalil.listenloud.repository.TagRepository;
import com.lilkhalil.listenloud.repository.LikeRepository;
import com.lilkhalil.listenloud.repository.UserTagRepository;

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

    private final LikeRepository likeRepository;

    private final MusicTagRepository musicTagRepository;

    private final UserTagRepository userTagRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final TagRepository tagRepository;

    private final SaveRepository saveRepository;

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

        return musicTagRepository.findMusicByTags(tags).stream()
            .map(musicMapper::toDto)
            .toList();
    }

    public List<MusicDTO> getRelevantSongs() {
        
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Tag> tags = userTagRepository.findByUser(user);

        if (tags.isEmpty()) {
            return Stream.concat(likeRepository.findAllOrderByLikesCount().stream(), musicRepository.findAll().stream())
                .distinct()
                .map(musicMapper::toDto)
                .toList();
        }

        return musicTagRepository.findMusicByTags(tags).stream().map(musicMapper::toDto).toList();
    }

    public List<MusicDTO> getSongsBySubscriptions() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return subscriptionRepository.findSubscriptionsByUser(user)
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

        return saveRepository.findByUser(user).stream().map(musicMapper::toDto).toList();
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

            List<MusicTag> musicTags = tags
                .stream()
                .map(tag -> tagRepository.findByName(TagType.valueOf(tag)).orElse(null))
                .map(tag -> new MusicTag(new MusicTagKey(music.getId(), tag.getId()), music, tag))
                .toList(); 

            musicTagRepository.saveAll(musicTags);
        }

        return musicMapper.toDto(music);
    }

    public MusicDTO saveSong(Long id) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicRepository.findById(id).orElse(null);

        Save save = new Save(new SaveKey(user.getId(), music.getId()), user, music, LocalDateTime.now());

        saveRepository.save(save);

        return musicMapper.toDto(music);

    }

    public MusicDTO editSong(Long id, String name, String description, MultipartFile image, MultipartFile audio, List<String> tags) throws IOException, NotValidContentTypeException
    {
        Music music = musicRepository.findById(id).orElse(null);

        music.setName(name == null ? music.getName() : name);
        music.setDescription(description == null ? music.getDescription() : description);

        if (tags != null) {
            musicTagRepository.deleteByMusic(music);

            List<MusicTag> musicTags = tags
                .stream()
                .map(tag -> tagRepository.findByName(TagType.valueOf(tag)).orElse(null))
                .map(tag -> new MusicTag(new MusicTagKey(music.getId(), tag.getId()), music, tag))
                .toList();

            musicTagRepository.saveAll(musicTags);
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

        likeRepository.deleteByMusic(music);

        musicTagRepository.deleteByMusic(music);

        saveRepository.deleteByMusic(music);

        musicRepository.deleteById(id);

        return musicMapper.toDto(music);
    }

    public void deleteSongs() throws IOException {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Music> music = musicRepository.findByAuthor(user);

        music.forEach(song -> {
            storageService.delete(song.getAudio());
            storageService.delete(song.getImage());
            likeRepository.deleteByMusic(song);
            musicTagRepository.deleteByMusic(song);
            saveRepository.deleteByMusic(song);
        });

        musicRepository.deleteAll(music);

    }

    public MusicDTO deleteSavedSong(Long id) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicRepository.findById(id).orElse(null);

        saveRepository.deleteByMusicAndUser(music, user);

        return musicMapper.toDto(music);
    }

    public void deleteSavedSongs() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        saveRepository.deleteByUser(user);

    }

    public void rateMusic(Long id) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Music music = musicRepository.findById(id).orElse(null);

        if (likeRepository.isLikedByUser(user, music)) 
            likeRepository.deleteById(new LikeKey(user.getId(), music.getId()));
        else 
            likeRepository.save(new Like(new LikeKey(user.getId(), music.getId()), user, music));

    }

}
