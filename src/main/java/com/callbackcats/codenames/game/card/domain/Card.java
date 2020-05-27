package com.callbackcats.codenames.game.card.domain;

import com.callbackcats.codenames.game.domain.Game;
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

    private String word;

    private CardType type;

    private boolean isFound;

    @ManyToOne
    private Game game;
}
