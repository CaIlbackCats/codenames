package com.callbackcats.codenames.game.team.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stat")
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "statistics")
    private Team team;

    @OneToMany(mappedBy = "statistics", cascade = CascadeType.ALL)
    private List<TurnStat> turnStats = new ArrayList<>();

    private Integer numOfCivilians = 0;
    private Integer numOfEnemySpies = 0;

    private Integer teamRounds = 0;

    private Integer maxFoundInARow = 0;
    private Integer minFoundInARow = 25;

    private Integer numOfPasses = 0;
    private Integer numOfInvalidVotes = 0;

}
