package com.callbackcats.codenames.game.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "board")
public class Board {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    private Game game;

    @ElementCollection
    private List<String> codewords;

}
