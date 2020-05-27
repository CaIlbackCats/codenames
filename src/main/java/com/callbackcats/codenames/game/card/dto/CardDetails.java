package com.callbackcats.codenames.game.card.dto;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.card.domain.CardType;
import com.callbackcats.codenames.game.domain.Word;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardDetails {

    private Long id;
    private WordDetails word;
    private String type;
    private boolean isFound;
    private Integer vote;

    public CardDetails(Card card) {
        this.id = card.getId();
        this.word = new WordDetails(card.getWord());
        this.type = card.getType().name();
        this.isFound = card.isFound();
        this.vote = card.getVote();
    }

    public CardDetails(Word word, CardType type) {
        this.word = new WordDetails(word);
        this.type = type.toString();
    }
}
