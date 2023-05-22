package com.lilkhalil.listenloud.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lilkhalil.listenloud.dto.MessageDTO;
import com.lilkhalil.listenloud.mapper.MessageMapper;
import com.lilkhalil.listenloud.model.Message;
import com.lilkhalil.listenloud.model.User;
import com.lilkhalil.listenloud.repository.MessageRepository;
import com.lilkhalil.listenloud.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final MessageMapper messageMapper;

    public MessageDTO createMessage(Long id, String content) {
        
        User sender = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User recipient = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User cannot be found!"));

        if (sender.getUsername().equals(recipient.getUsername())) throw new IllegalArgumentException("You cannot send message to yourself!");

        Message message = Message.builder()
            .sender(sender)
            .recipient(recipient)
            .content(content)
            .timestamp(LocalDateTime.now())
            .isReaded(false)
            .build();

        return messageMapper.toDto(messageRepository.save(message));

    }

    public List<MessageDTO> getDialogues() {

        User sender = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Long> ids = messageRepository.findLatestMessageOfDialogue(sender);

        return messageRepository.findAllById(ids).stream().map(messageMapper::toDto).toList();
    }

    public List<MessageDTO> getMessages(Long id) {

        User sender = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User recipient = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User cannot be found!"));

        List<Message> messages = messageRepository.findBySenderInAndRecipientInOrderByTimestamp(List.of(sender, recipient), List.of(sender, recipient));

        messages = messages.stream()
            .peek(message -> {
                if (message.getRecipient().getUsername().equals(sender.getUsername()) && !message.getIsReaded()) message.setIsReaded(true);
            })
            .toList();

        return messageRepository.saveAll(messages).stream().map(messageMapper::toDto).toList();
    }

    public void deleteDialogue(Long id) {

        User sender = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User recipient = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User cannot be found!"));

        List<Message> messages = messageRepository.findBySenderInAndRecipientInOrderByTimestamp(List.of(sender, recipient), List.of(sender, recipient));

        messageRepository.deleteAll(messages);
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    public MessageDTO editMessage(Long id, String content) {
        
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Message cannot be found!"));

        message.setContent(content);

        message.setTimestamp(LocalDateTime.now());

        return messageMapper.toDto(messageRepository.save(message));
    }

}
