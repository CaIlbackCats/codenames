import { CardVoteModel } from "@/models/game/card/cardVoteModel";

export interface TeamVoteModel {
  gameId: number;
  votingTeam: string;
  cardVotes: Array<CardVoteModel>;
}
