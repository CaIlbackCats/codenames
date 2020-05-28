import {CardDetailsModel} from "@/models/game/card/cardDetailsModel";

export interface GameCreationModel {
    id: number,
    board: Array<CardDetailsModel>,
}