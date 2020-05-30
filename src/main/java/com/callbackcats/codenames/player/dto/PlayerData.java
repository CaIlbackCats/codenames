package com.callbackcats.codenames.player.dto;

import com.callbackcats.codenames.player.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerData {

    private Long id;

    private Boolean lobbyOwner;

    private String name;

    private String role;

    private String side;

    private String lobbyId;

    private Boolean rdyState;

    private Boolean passed;

    public PlayerData(Player player) {
        this.id = player.getId();
        this.lobbyOwner = player.getLobbyOwner();
        this.name = player.getName();
        this.role = player.getRole().toString();
        this.side = player.getSide().toString();
        if (player.getLobby() != null) {
            this.lobbyId = player.getLobby().getId();
        }
        this.rdyState = player.getRdyState();
        this.passed = player.getPassed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
