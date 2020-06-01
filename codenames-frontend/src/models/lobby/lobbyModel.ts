import {PlayerModel} from "@/models/player/playerModel";

export interface LobbyModel {
    id: string,
    everyoneRdy: boolean,
    players: Array<PlayerModel>,
    currentGameId: number,
    kickingPhase: boolean,
    gameLanguage:string,
}