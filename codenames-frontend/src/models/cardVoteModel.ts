import {CardDetailsModel} from "@/models/cardDetailsModel";

export interface CardVoteModel {

    votedCard: CardDetailsModel,
    votedPlayersId: Array<number>,
}