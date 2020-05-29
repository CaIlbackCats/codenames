import {TeamModel} from "@/models/teamModel";
import {CardDetailsModel} from "@/models/game/card/cardDetailsModel";

export interface GameStateModel {

    id: number,
    board: Array<CardDetailsModel>,
    endGame: boolean,
    endTurn: boolean,
    winnerTeam: string,
    gameEndByAssassin: boolean,
    startingTeamColor: string,
    teams: Array<TeamModel>,
    currentTeam: string,
    active: boolean;
    votingPhase: boolean,
    lobbyId?: string,
    civiliansFoundByBlueTeam: number,
    civiliansFoundByRedTeam: number,
    rounds: number,

}