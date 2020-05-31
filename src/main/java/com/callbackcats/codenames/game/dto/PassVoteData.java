package com.callbackcats.codenames.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PassVoteData {

    private Long playerId;

    private Boolean passed;

}
