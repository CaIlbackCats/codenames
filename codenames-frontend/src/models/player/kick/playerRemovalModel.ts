import {PlayerModel} from "@/models/player/playerModel";

export interface PlayerRemovalModel {
    playerInitId?: number,
    playerToRemoveId?: number,
    vote?: boolean,
    playerToRemove?: PlayerModel,
}