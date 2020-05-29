package com.callbackcats.codenames.game.domain;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.domain.Player;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "game")
    private List<Card> board;

    @Column(name = "is_end_game")
    private Boolean endGame;

    @Column(name = "is_end_turn")
    private Boolean endTurn;

    @Column(name = "winning_team")
    private SideType winner;

    @Column(name = "is_assassing_found")
    private Boolean endGameByAssassin;

    @Column(name = "start_team_color")
    private SideType startingTeamColor;

    @OneToMany(mappedBy = "game")
    private List<Team> teams;

    @Column(name = "current_team_turn")
    private SideType currentTeam;

    @Column(name = "is_active")
    private Boolean active;

    public Game(List<Card> board, List<Team> teams) {
        this.board = board;
        this.teams = teams;
        this.teams = new ArrayList<>();
        this.endGame = false;
        this.endTurn = false;
        active = true;
    }

}
