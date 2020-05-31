package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.domain.GameTurn;
import com.callbackcats.codenames.game.repository.GameTurnRepository;
import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GameTurnService {

    private final GameTurnRepository gameTurnRepository;

    public GameTurnService(GameTurnRepository gameTurnRepository) {
        this.gameTurnRepository = gameTurnRepository;
    }

    public void createGameTurnToGame(Game game) {
        GameTurn gameTurn = new GameTurn(game);
        gameTurnRepository.save(gameTurn);
    }

    public void changeTurn(GameTurn gameTurn) {
        SideType oppositeTeam = SideType.getOppositeSide(gameTurn.getCurrentTeam());
        gameTurn.setCurrentTeam(oppositeTeam);
        gameTurn.setCurrentRole(RoleType.SPYMASTER);
        gameTurnRepository.save(gameTurn);
    }

    public void advanceToSpyTurn(GameTurn gameTurn) {
        gameTurn.setCurrentRole(RoleType.SPY);
        gameTurnRepository.save(gameTurn);
    }
}
