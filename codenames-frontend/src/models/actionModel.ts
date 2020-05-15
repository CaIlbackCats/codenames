import {PlayerModel} from "@/models/playerModel";
import {PlayerRemovalModel} from "@/models/playerRemovalModel";
import {LobbyModel} from "@/models/lobbyModel";

export interface ActionModel {
    actionToDo: string,
    currentPlayer: PlayerModel,
    playerList: Array<PlayerModel>,
    playerRemoval: PlayerRemovalModel,
    lobbyDetails: LobbyModel,

}