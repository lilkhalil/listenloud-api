package com.lilkhalil.listenloud.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lilkhalil.listenloud.model.ExceptionResponse;
import com.lilkhalil.listenloud.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;

    @PostMapping("send/{id}")
    public ResponseEntity<?> sendMessage(@PathVariable Long id, @RequestParam String content) {
        try {
            return new ResponseEntity<>(messageService.createMessage(id, content), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dialogues")
    public ResponseEntity<?> readDialogues() {
        return new ResponseEntity<>(messageService.getDialogues(), HttpStatus.OK);
    }

    @GetMapping("/dialogues/{id}")
    public ResponseEntity<?> readMessagesOfDialogue(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(messageService.getMessages(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("dialogues/{id}")
    public ResponseEntity<?> deleteDialogue(@PathVariable Long id) {
        try {
            messageService.deleteDialogue(id);
            return new ResponseEntity<>("Dialogue was successfully deleted!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("dialogues/{id}/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") Long id) {
        try {
            messageService.deleteMessage(id);
            return new ResponseEntity<>("Message was successfully deleted!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("dialogues/{id}/{messageId}")
    public ResponseEntity<?> editMessage(@PathVariable("messageId") Long id, @RequestParam String content) {
        try {
            return new ResponseEntity<>(messageService.editMessage(id, content), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                ExceptionResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .timestamp(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
