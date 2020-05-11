package com.callbackcats.codenames.player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRemovalData {

    private Long ownerId;
    private Long playerToRemoveId;
    private Boolean vote;
}
