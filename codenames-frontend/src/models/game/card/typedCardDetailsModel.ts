import {WordDetailModel} from "@/models/game/card/wordDetailModel";

export interface TypedCardDetailsModel {
    id:number,
    word: WordDetailModel,
    type: string,
    isFound: boolean,
    voted:boolean,
    voteCounter:number,
}