package com.callbackcats.codenames.game.card.domain;

import com.callbackcats.codenames.lobby.player.domain.SideType;
import lombok.Getter;

@Getter
public enum CardType {
    BLUE_SPY(SideType.BLUE),
    RED_SPY(SideType.RED),
    BYSTANDER(SideType.NOT_SELECTED),
    ASSASSIN(SideType.NOT_SELECTED);

    private SideType teamColorValue;

    CardType(SideType teamColorValue) {
    }
}
