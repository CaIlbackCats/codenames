package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GameDetails {

    private Long id;
    private List<CardDetails> board;

    public GameDetails(Game game) {
        this.id = game.getId();
        this.board = game.getBoard().stream().map(CardDetails::new).collect(Collectors.toList());
    }
}
