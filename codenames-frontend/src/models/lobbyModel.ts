import {PlayerModel} from "@/models/playerModel";

export interface LobbyModel {
    id: string,
    everyoneRdy: boolean,
    players: Array<PlayerModel>,
}