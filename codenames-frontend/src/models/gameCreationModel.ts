import {CardDetailsModel} from "@/models/cardDetailsModel";

export interface GameCreationModel {
    id: number,
    board: Array<CardDetailsModel>,
    blueScore: number,
    redScore: number,
    civiliansFoundByBlueTeam: number,
    civiliansFoundByRedTeam: number,
    rounds: number
}