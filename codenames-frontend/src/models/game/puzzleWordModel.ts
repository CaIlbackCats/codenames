export interface PuzzleWordModel {

    id: number,
    puzzleWord: string,
    maxGuessCount: number,
    usedGuesses: number,
    wordRegisterTime: Date,
}