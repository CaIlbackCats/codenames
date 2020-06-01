import { PlayerModel } from "@/models/player/playerModel";

export interface LobbyModel {
<<<<<<< HEAD
    id: string,
    everyoneRdy: boolean,
    players: Array<PlayerModel>,
    currentGameId: number,
    kickingPhase: boolean,
    gameLanguage:string,
}
=======
  id: string;
  everyoneRdy: boolean;
  players: Array<PlayerModel>;
  currentGameId: number;
  kickingPhase: boolean;
}
>>>>>>> set hungarian language option with i18n
