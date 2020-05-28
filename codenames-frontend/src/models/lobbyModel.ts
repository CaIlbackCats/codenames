import {PlayerModel} from "@/models/playerModel";

export interface LobbyModel {
    id: string,
    everyoneRdy: boolean,
    players: Array<PlayerModel>,
    currentGameId: number,
    kickingPhase: boolean,
}