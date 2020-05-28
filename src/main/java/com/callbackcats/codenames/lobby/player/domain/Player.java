package com.callbackcats.codenames.lobby.player.domain;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.dto.PlayerCreationData;
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

    @Column(name = "kick_vote")
    private Integer kickVoteCount;

    @Column(name = "ready")
    private Boolean rdyState;

    @Column(name = "visible")
    private Boolean visible;

    @OneToOne(mappedBy = "player")
    @JoinColumn(name = "voted_card_id")
    private Card votedCard;

    public Player(PlayerCreationData playerCreationData) {
        //  this.lobbyOwner = playerCreationData.getLobbyOwner();
        this.name = playerCreationData.getName();
        this.role = RoleType.NOT_SELECTED;
        this.side = SideType.NOT_SELECTED;
        this.kickVoteCount = 0;
        this.rdyState = false;
        this.visible = true;
    }
}
