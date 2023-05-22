package com.lilkhalil.listenloud.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lilkhalil.listenloud.dto.UserDTO;
import com.lilkhalil.listenloud.exception.NotValidContentTypeException;
import com.lilkhalil.listenloud.mapper.UserMapper;
import com.lilkhalil.listenloud.model.Subscription;
import com.lilkhalil.listenloud.model.SubscriptionKey;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.SubscriptionRepository;
import com.lilkhalil.listenloud.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    
    private final UserRepository userRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final StorageService storageService;

    public UserDTO getUser() {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userMapper.toDto(user);
    }

    public UserDTO updateUser(Long id, String username, String biography, MultipartFile image) throws NotValidContentTypeException, IOException {
        
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        user.setUsername(username == null ? user.getUsername() : username);
        user.setBiography(biography == null ? user.getBiography() : biography);

        if (image == null)
            user.setImage(user.getImage());
        else {
            storageService.isValidMediaType(image);
            storageService.delete(user.getImage());
            user.setImage(storageService.upload(image));
        }

        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserDTO> getSubscriptions() {

        User subscriber = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return subscriptionRepository.findSubscriptionsByUser(subscriber).stream().map(userMapper::toDto).toList();
    }

    public List<UserDTO> getSubscribers() {

        User publisher = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return subscriptionRepository.findSubscribersByUser(publisher).stream().map(userMapper::toDto).toList();
    }

    public UserDTO subscribe(Long id) {

        User subscriber = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User publisher = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User cannot be found!"));

        subscriptionRepository.save(new Subscription(new SubscriptionKey(subscriber.getId(), publisher.getId()), subscriber, publisher, LocalDateTime.now()));

        return userMapper.toDto(publisher);
    }

    public UserDTO unsubscribe(Long id) {

        User subscriber = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User publisher = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User cannot be found!"));

        subscriptionRepository.deleteBySubscriberAndPublisher(subscriber, publisher);

        return userMapper.toDto(publisher);
    }
    
    public List<UserDTO> unsubscribeAllById(List<Long> ids) {

        User subscriber = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 

        List<User> publishers = userRepository.findAllById(ids);

        subscriptionRepository.deleteBySubscriberAndPublisherIn(subscriber, publishers);

        return publishers.stream().map(userMapper::toDto).toList();
    }

}
