import {PlayerModel} from "@/models/playerModel";

export interface RoomModel {
    id:number,
    players: Array<PlayerModel>;
}