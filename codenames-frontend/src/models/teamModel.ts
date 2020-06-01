import {PlayerModel} from "@/models/player/playerModel";
import {StatModel} from "@/models/game/statModel";

export interface TeamModel {

    id:number,
    side:string,
    score:number,
    players:Array<PlayerModel>,
    statistics: StatModel
}