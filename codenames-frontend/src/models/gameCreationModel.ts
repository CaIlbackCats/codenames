import {CardDetailsModel} from "@/models/cardDetailsModel";

export interface GameCreationModel {
    id: number,
    board: Array<CardDetailsModel>
}