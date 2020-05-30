package com.callbackcats.codenames.player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
