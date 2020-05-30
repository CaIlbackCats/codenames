package com.callbackcats.codenames.game.team.domain;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.SideType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "side")
    @Enumerated(EnumType.STRING)
    private SideType side;

    @Column(name = "score")
    private Integer score;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "team")
    private List<Player> players;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public Team(List<Player> players, SideType side, Game game) {
        this.players = players;
        this.side = side;
        this.game = game;
        this.score = 0;
    }
}
