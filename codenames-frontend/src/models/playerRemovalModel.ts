import {PlayerModel} from "@/models/playerModel";

export interface PlayerRemovalModel {
    ownerId?: number,
    playerToRemoveId?: number,
    vote?: boolean,
    playerToRemove?:PlayerModel,
}