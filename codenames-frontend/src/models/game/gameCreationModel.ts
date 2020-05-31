import {TypedCardDetailsModel} from "@/models/game/card/typedCardDetailsModel";

export interface GameCreationModel {
    id: number,
    board: Array<TypedCardDetailsModel>,
}