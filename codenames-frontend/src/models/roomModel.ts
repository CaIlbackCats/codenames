import {PlayerModel} from "@/models/playerModel";
import {Client} from "webstomp-client";

export interface RoomModel {
    stompClient: Client,
    name: string,
    playerName?: string,

}