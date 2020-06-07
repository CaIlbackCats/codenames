package com.callbackcats.codenames.game.team.dto;

import java.time.LocalDateTime;

public class TurnStatData {

    private Long id;

    private StatData statistics;

    private LocalDateTime spyMasterStartTime = LocalDateTime.now();
    private LocalDateTime spyMasterEndTime;

    private LocalDateTime spyStartTime = LocalDateTime.now();
    private LocalDateTime spyEndTime;

    private Integer numOfSpies;
}
