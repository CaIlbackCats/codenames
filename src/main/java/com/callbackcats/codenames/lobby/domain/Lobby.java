package com.callbackcats.codenames.lobby.domain;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.player.domain.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "lobby")
public class Lobby {

    @Id
    private String id = UUID.randomUUID().toString();

    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "lobby")
    private List<Player> playerList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "lobby")
    private List<Game> games;

}
