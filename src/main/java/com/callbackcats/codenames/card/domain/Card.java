package com.callbackcats.codenames.card.domain;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.player.domain.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    private CardType type;

    @Column(name = "is_found")
    private boolean isFound;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    // private Integer vote;

    @OneToMany(mappedBy = "votedCard")
    private List<Player> players;

    @Column(name = "is_voted")
    private Boolean voted=false;

    @Column(name = "number_of_votes")
    private Integer voteCounter=0;

    public Card(Word word, CardType cardType, Game game) {
        this.word = word;
        this.type = cardType;
        this.game = game;
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
