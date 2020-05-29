package com.callbackcats.codenames.game.card.dto;

import com.callbackcats.codenames.game.card.dto.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardVoteData {

    private Long votedCardId;
    private Long votedPlayerId;
}
