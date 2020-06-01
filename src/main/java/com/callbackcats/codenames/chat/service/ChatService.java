package com.callbackcats.codenames.chat.service;

import com.callbackcats.codenames.chat.dto.MessageData;
import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {

    private final PlayerService playerService;

    public ChatService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public MessageData addPlayerInfoToMessage(MessageData messageData) {

        PlayerData player = playerService.findPlayerDataById(messageData.getPlayerId());
        messageData.setTeamColor(player.getSide());
        log.info("Message side added");

        return messageData;
    }
}
