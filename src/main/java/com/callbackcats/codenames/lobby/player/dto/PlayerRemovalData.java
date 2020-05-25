package com.callbackcats.codenames.lobby.player.dto;

import com.callbackcats.codenames.lobby.player.domain.KickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PlayerRemovalData {

    private Long ownerId;
    private Long playerToRemoveId;
    private Boolean vote;
    private PlayerData playerToRemove;
}
