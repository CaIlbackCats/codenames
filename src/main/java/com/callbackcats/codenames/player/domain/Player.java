package com.callbackcats.codenames.player.domain;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.player.dto.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "player")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "lobby_owner")
    private Boolean lobbyOwner;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private SideType side;

    @ManyToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    public Player(String name) {
        this.name = name;
        this.role = RoleType.NOT_SELECTED;
        this.side = SideType.NOT_SELECTED;
    }
}
