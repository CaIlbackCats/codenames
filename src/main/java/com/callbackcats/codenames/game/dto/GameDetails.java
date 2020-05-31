package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameDetails {

    private Long id;

    public GameDetails(Game game) {
        this.id = game.getId();
    }
}
