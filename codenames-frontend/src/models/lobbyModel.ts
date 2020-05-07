import {PlayerModel} from "@/models/playerModel";

export interface LobbyModel {
    id: string,
    players?: Array<PlayerModel>;
}