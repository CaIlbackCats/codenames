import {PlayerModel} from "@/models/playerModel";
import {PlayerRemovalModel} from "@/models/playerRemovalModel";
import {LobbyModel} from "@/models/lobbyModel";
import {LobbyTeamModel} from "@/models/lobbyTeamModel";
import {GameCreationModel} from "@/models/gameCreationModel";

export interface ActionModel {
    actionToDo: string,
    currentPlayer: PlayerModel,
    playerRemoval: PlayerRemovalModel,
    lobbyDetails: LobbyModel,
    lobbyTeamData: LobbyTeamModel,
    gameDetails: GameCreationModel

}