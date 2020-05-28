package com.callbackcats.codenames.game.card.domain;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.domain.Word;
import com.callbackcats.codenames.lobby.player.domain.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "card")
    @JoinColumn(name = "word_id")
    private Word word;

    private CardType type;

    private boolean isFound;

    @ManyToOne
    private Game game;

    private Integer vote;

    @OneToOne
    private Player player;

    public Card(Word word, CardType cardType) {
        this.word = word;
        this.type = cardType;
    }
}
