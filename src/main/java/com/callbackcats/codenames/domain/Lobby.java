package com.callbackcats.codenames.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

}
