import {PlayerModel} from "@/models/player/playerModel";
import {PuzzleWordModel} from "@/models/game/puzzleWordModel";

export interface TeamModel {

    id:number,
    side:string,
    score:number,
    players:Array<PlayerModel>,
    puzzleWords: Array<PuzzleWordModel>,

}