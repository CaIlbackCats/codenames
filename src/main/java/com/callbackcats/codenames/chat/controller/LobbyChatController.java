package com.callbackcats.codenames.chat.controller;

import com.callbackcats.codenames.chat.dto.MessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.persistence.EntityNotFoundException;

@Controller
@Slf4j
public class LobbyChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public LobbyChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/public")
   // @SendTo("/topic/chat")
    public MessageData handleMessage(@Payload MessageData messageData) {
        if(messageData.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        simpMessagingTemplate.convertAndSend("/chat/" + messageData.getLobbyName(), messageData);
        return messageData;
    }

}
