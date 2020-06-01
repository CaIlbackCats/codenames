import { PlayerModel } from "@/models/player/playerModel";

export interface PlayerRemovalModel {
  ownerId?: number;
  playerToRemoveId?: number;
  vote?: boolean;
  playerToRemove?: PlayerModel;
}
