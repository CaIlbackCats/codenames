import {WordDetailModel} from "@/models/game/card/wordDetailModel";

export interface CardDetailsModel {
    id:number,
    word: WordDetailModel,
    type: string,
    isFound: boolean
}