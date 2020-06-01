import {WordDetailModel} from "@/models/game/card/wordDetailModel";

export interface TypedCardDetailsModel {
    id: number,
    word: WordDetailModel,
    type: string,
    found: boolean,
    voted: boolean,
    voteCounter: number,
}