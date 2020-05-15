import {PlayerModel} from "@/models/playerModel";

export interface LobbyModel {
    id: string,
    everyOneRdy: boolean,
    players?: Array<PlayerModel>,
}