import {PlayerModel} from "@/models/playerModel";
import {PlayerRemovalModel} from "@/models/playerRemovalModel";

export interface ActionModel {
    actionToDo: string,
    currentPlayer: PlayerModel,
    playerList: Array<PlayerModel>,
    playerRemoval: PlayerRemovalModel,

}