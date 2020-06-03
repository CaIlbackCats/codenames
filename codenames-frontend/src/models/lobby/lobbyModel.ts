import {PlayerModel} from "@/models/player/playerModel";
import {RemainingRoleModel} from "@/models/lobby/remainingRoleModel";

export interface LobbyModel {
    id: string,
    everyoneRdy: boolean,
    players: Array<PlayerModel>,
    currentGameId: number,
    kickingPhase: boolean,
    gameLanguage: string,
    remainingRole: RemainingRoleModel,
}