package com.callbackcats.codenames.game.domain;

import com.callbackcats.codenames.card.domain.Card;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.player.domain.SideType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "game")
    private List<Card> board = new ArrayList<>();

    @Column(name = "is_end_game")
    private Boolean endGame = false;

    @Column(name = "is_end_turn")
    private Boolean endTurn = false;

    @Column(name = "winning_team")
    @Enumerated(EnumType.STRING)
    private SideType winner;

    @Column(name = "is_assassing_found")
    private Boolean endGameByAssassin = false;

    @Column(name = "start_team_color")
    @Enumerated(EnumType.STRING)
    private SideType startingTeam;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "game")
    private List<Team> teams = new ArrayList<>();

    @Column(name = "current_team_turn")
    @Enumerated(EnumType.STRING)
    private SideType currentTeam;

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "is_voting_phase_on")
    private Boolean votingPhase = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "game")
    private List<PuzzleWord> puzzleWords;

    private Integer passVoteCounter = 0;

    public Game(Lobby lobby) {
        this.startingTeam = SideType.getRandomSide();
        this.currentTeam = startingTeam;
        this.lobby = lobby;
    }

}
