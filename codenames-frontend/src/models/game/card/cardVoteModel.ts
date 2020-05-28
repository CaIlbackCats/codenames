import {CardDetailsModel} from "@/models/game/card/cardDetailsModel";

export interface CardVoteModel {

    votedCard: CardDetailsModel,
    votedPlayersId: Array<number>,
}