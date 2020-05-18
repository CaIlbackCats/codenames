package com.callbackcats.codenames.lobby.player.dto;

import com.callbackcats.codenames.lobby.player.domain.KickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRemovalData {

    private KickType kickType;
    private List<PlayerData> votingPlayers;
    private Long ownerId;
    private Long playerToRemoveId;
    private Boolean vote;
}
