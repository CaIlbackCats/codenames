package com.callbackcats.codenames.chat.controller;

import com.callbackcats.codenames.chat.dto.MessageData;
import com.callbackcats.codenames.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/public/{lobbyId}")
    @SendTo("/chat/{lobbyId}")
    public MessageData handleMessage(@DestinationVariable String lobbyId, @Payload MessageData messageData) {
        log.info("Chat message handling requested");
        if(!messageData.getMessage().isEmpty()) {
            chatService.addPlayerInfoToMessage(messageData);
            return messageData;
        }
        return null;
    }
}
