package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.card.dto.CardVoteData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamVoteData {


    private Long gameId;
    private String votingTeam;
    private List<CardVoteData> cardVotes;
}
