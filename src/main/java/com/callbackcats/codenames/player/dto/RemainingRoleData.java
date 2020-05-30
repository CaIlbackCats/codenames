package com.callbackcats.codenames.player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class RemainingRoleData {

    private Boolean blueSpymaster;

    private Boolean blueSpy;

    private Boolean redSpymaster;

    private Boolean redSpy;
}
