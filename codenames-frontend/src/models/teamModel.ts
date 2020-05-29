import {PlayerModel} from "@/models/playerModel";

export interface TeamModel {

    id:number,
    side:string,
    score:number,
    players:Array<PlayerModel>,
}