import {WordDetailModel} from "@/models/game/card/wordDetailModel";

export interface TypelessCardDetailsModel {
    id: number,
    word: WordDetailModel,
    found: boolean,
    type: string,
    voted: boolean,
    voteCounter: number,
}