import {WordDetailModel} from "@/models/wordDetailModel";

export interface CardDetailsModel {
    id:number,
    word: WordDetailModel,
    type: string,
    isFound: boolean
}