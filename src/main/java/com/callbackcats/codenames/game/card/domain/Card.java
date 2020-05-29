package com.callbackcats.codenames.game.card.domain;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.player.domain.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "word_id")
    private Word word;

    private CardType type;

    private boolean isFound;

    @ManyToOne
    private Game game;

    private Integer vote;

    @OneToOne(mappedBy = "votedCard")
    private Player player;

    public Card(Word word, CardType cardType) {
        this.word = word;
        this.type = cardType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
