package com.callbackcats.codenames.lobby.player.domain;

import com.callbackcats.codenames.game.domain.CardType;

public enum SideType {
    BLUE,
    RED,
    NOT_SELECTED;

    public static SideType getRandomSide() {
        int randomIndex = (int) Math.floor(Math.random() * (SideType.values().length - 1));
        return SideType.values()[randomIndex];
    }
}
