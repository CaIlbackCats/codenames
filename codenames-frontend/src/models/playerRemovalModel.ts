import {PlayerModel} from "@/models/playerModel";

export interface PlayerRemovalModel {
    kickType?: string,
    votingPlayers?: Array<PlayerModel>,
    ownerId?: number,
    playerToRemoveId?: number,
    vote?: boolean,

}