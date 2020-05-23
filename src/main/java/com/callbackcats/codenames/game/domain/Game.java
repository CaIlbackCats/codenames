package com.callbackcats.codenames.game.domain;

import com.callbackcats.codenames.lobby.domain.Lobby;
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

}
