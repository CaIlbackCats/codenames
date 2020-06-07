package com.callbackcats.codenames.card.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GameLanguage {
    ENGLISH("en"),
    HUNGARIAN("hu");

    private String shortValue;

    GameLanguage(String shortValue) {
        this.shortValue = shortValue;
    }

    public static GameLanguage getLanguageByValue(String shortValue) {
        return Arrays.stream(GameLanguage.values())
                .filter(gameLanguage -> gameLanguage.shortValue.equals(shortValue))
                .findFirst().orElse(GameLanguage.ENGLISH);
    }

}
