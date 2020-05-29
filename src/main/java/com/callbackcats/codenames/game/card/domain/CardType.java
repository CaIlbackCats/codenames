package com.callbackcats.codenames.game.card.domain;

import com.callbackcats.codenames.lobby.player.domain.SideType;
import lombok.Getter;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum CardType {
    BLUE_SPY(SideType.BLUE),
    RED_SPY(SideType.RED),
    BYSTANDER(SideType.NOT_SELECTED),
    ASSASSIN(SideType.NOT_SELECTED);

    private SideType teamColorValue;

    CardType(SideType teamColorValue) {
        this.teamColorValue = teamColorValue;
    }

    public static CardType getRandomCardType() {
        int randomIndex = (int) Math.floor(Math.random() * CardType.values().length);
        return CardType.values()[randomIndex];

    }

    public static CardType findCardTypeBySide(SideType sideType) {

        return Arrays.stream(CardType.values())
                .filter(card -> card.getTeamColorValue().equals(sideType))
                .findFirst().orElseThrow(() -> new RuntimeException("CardTyoe not found"));
    }

    public static CardType selectOpposite(SideType sideType) {
        CardType opposite;
        if (sideType == SideType.BLUE) {
            opposite = CardType.RED_SPY;
        } else {
            opposite = CardType.BLUE_SPY;
        }
        return opposite;
    }


}
