<<<<<<< HEAD
import {TeamModel} from "@/models/teamModel";
import {TypedCardDetailsModel} from "@/models/game/card/typedCardDetailsModel";
import {GameTurnModel} from "@/models/game/gameTurnModel";

export interface GameStateModel {

    id: number,
    endGame: boolean,
    endTurn: boolean,
    winnerTeam: string,
    gameEndByAssassin: boolean,
    startingTeamColor: string,
    teams: Array<TeamModel>,
    active: boolean;
    votingPhase: boolean,
    lobbyId?: string,
    civiliansFoundByBlueTeam: number,
    civiliansFoundByRedTeam: number,
    rounds: number,
    passVoteCounter: number,
    gameTurnData: GameTurnModel,
}
=======
import { TeamModel } from "@/models/teamModel";
import { CardDetailsModel } from "@/models/game/card/cardDetailsModel";
import { PuzzleWordModel } from "@/models/game/puzzleWordModel";

export interface GameStateModel {
  id: number;
  board: Array<CardDetailsModel>;
  endGame: boolean;
  endTurn: boolean;
  winnerTeam: string;
  gameEndByAssassin: boolean;
  startingTeamColor: string;
  teams: Array<TeamModel>;
  currentTeam: string;
  active: boolean;
  votingPhase: boolean;
  lobbyId?: string;
  civiliansFoundByBlueTeam: number;
  civiliansFoundByRedTeam: number;
  rounds: number;
  puzzleWords: Array<PuzzleWordModel>;
}
>>>>>>> set hungarian language option with i18n
