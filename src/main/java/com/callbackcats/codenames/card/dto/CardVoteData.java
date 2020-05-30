package com.callbackcats.codenames.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardVoteData {

    private Long votedCardId;
    private Long votedPlayerId;
}
