package com.callbackcats.codenames.player.domain;

import java.util.Random;

public enum SideType {
    BLUE,
    RED,
    NOT_SELECTED;

    public static SideType getRandomSide() {
        int randomIndex = (int) Math.floor(Math.random() * (SideType.values().length - 1));
        return SideType.values()[randomIndex];
    }
}
