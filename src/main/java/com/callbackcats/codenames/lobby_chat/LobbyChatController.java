package com.callbackcats.codenames.lobby_chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class LobbyChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public LobbyChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/public")
    @SendTo("/topic/chat")
    public MessageData handleMessage(@Payload MessageData messageData) {
        log.info(messageData.getMessage());
        return messageData;
    }

}
