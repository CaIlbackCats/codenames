import {CardVoteModel} from "@/models/cardVoteModel";

export interface TeamVoteModel {
    gameId: number,
    votingTeam: string,
    cardVotes: Array<CardVoteModel>,
}