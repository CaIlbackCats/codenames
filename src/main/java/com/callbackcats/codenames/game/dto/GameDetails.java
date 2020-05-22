package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GameDetails {

    private Long id;
    private List<CardData> board;

    public GameDetails(Game game) {
        this.id = game.getId();
    }
}
