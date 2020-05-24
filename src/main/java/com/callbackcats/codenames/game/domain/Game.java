package com.callbackcats.codenames.game.domain;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(name = "blue_score")
    private Integer blueScore = 0;

    @Column(name = "red_score")
    private Integer redSCore = 0;

    @Column(name = "is_end_game")
    private Boolean endGame = false;

    @Column(name = "is_end_turn")
    private Boolean endTurn = false;

    @Column(name = "winning_team")
    private SideType winner;

    @Column(name = "is_assassing_found")
    private Boolean endGameByAssassin;

    @Column(name = "start_team_color")
    private SideType startingTeamColor;

}
