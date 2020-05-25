import {CardDetailsModel} from "@/models/cardDetailsModel";

export interface GameStateModel {

    id: number,
    board: Array<CardDetailsModel>,
    blueScore: number,
    redScore: number,
    endGame: boolean,
    endTurn: boolean,
    winnerTeam: string,
    gameEndByAssassin: boolean,
    startingTeamColor: string,
    lobbyId?: string,
}