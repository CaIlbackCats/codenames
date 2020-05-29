import {PlayerModel} from "@/models/player/playerModel";

export interface TeamModel {

    id:number,
    side:string,
    score:number,
    players:Array<PlayerModel>,
}