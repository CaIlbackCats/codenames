package com.callbackcats.codenames.game.domain;

import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_turn")
public class GameTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "current_team")
    @Enumerated(EnumType.STRING)
    private SideType currentTeam;

    @Column(name = "current_role")
    @Enumerated(EnumType.STRING)
    private RoleType currentRole;

    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public GameTurn(Game game) {
        this.game = game;
        this.currentTeam = game.getStartingTeam();
        this.currentRole = RoleType.SPYMASTER;
    }
}
