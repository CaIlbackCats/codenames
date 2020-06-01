import {TeamModel} from "@/models/teamModel";
import {TypedCardDetailsModel} from "@/models/game/card/typedCardDetailsModel";
import {GameTurnModel} from "@/models/game/gameTurnModel";

export interface GameStateModel {
    id: number,
    endGame: boolean,
    endTurn: boolean,
    winnerTeam: string,
    gameEndByAssassin: boolean,
    startingTeamColor: string,
    teams: Array<TeamModel>,
    active: boolean;
    votingPhase: boolean,
    lobbyId?: string,
    civiliansFoundByBlueTeam: number,
    civiliansFoundByRedTeam: number,
    rounds: number,
    passVoteCounter: number,
    gameTurnData: GameTurnModel,
}