package com.callbackcats.codenames.game.team.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "turn_stat")
public class TurnStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "statistics_id")
    private Stat statistics;

    private LocalDateTime spyMasterStartTime = LocalDateTime.now();
    private LocalDateTime spyMasterEndTime;

    private LocalDateTime spyStartTime;
    private LocalDateTime spyEndTime;

    private Integer numOfSpies = 0;
    private Integer numOfGuesses = 0;

    public TurnStat(Stat statistics) {
        this.statistics = statistics;
    }
}
