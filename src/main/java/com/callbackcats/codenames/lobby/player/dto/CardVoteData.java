package com.callbackcats.codenames.lobby.player.dto;

import com.callbackcats.codenames.game.card.dto.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardVoteData {

    private CardDetails votedCard;
    private List<Long> votedPlayersId;
}
