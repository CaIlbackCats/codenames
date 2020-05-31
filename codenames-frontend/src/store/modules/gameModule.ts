import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import * as websocket from '@/services/websocket';
import {config} from "@/config";
import {GameCreationModel} from "@/models/game/gameCreationModel";
import axios, {AxiosResponse} from 'axios';
import {CardDetailsModel} from "@/models/game/card/cardDetailsModel";
import {CardVoteModel} from "@/models/game/card/cardVoteModel";
import {GameStateModel} from "@/models/game/gameStateModel";
import {PuzzleWordModel} from "@/models/game/puzzleWordModel";
import {PassedVoteModel} from "@/models/player/passedVoteModel";

const BASE_URL = process.env.VUE_APP_BASE_URL;

@Module
export default class GameModule extends VuexModule {
    private game: GameStateModel = {
        id: -1,
        board: [],
        civiliansFoundByBlueTeam: 0,
        civiliansFoundByRedTeam: 0,
        rounds: 0,
        endGame: false,
        endTurn: false,
        winnerTeam: "",
        gameEndByAssassin: false,
        startingTeamColor: "",
        active: false,
        currentTeam: "",
        teams: [],
        votingPhase: false,
        puzzleWords: [],
        passVoteCounter: 0,
    }

    private cardVotes: Array<CardVoteModel> = [];

    @Action({rawError: true})
    public subscribeToGame() {
        //   const lobbyId: string = this.context.getters["lobbyId"];
        const gamePath: string = "/game/" + this.gameId;
        websocket.subscribe(gamePath, (body) => {
                if (body) {
                    this.context.commit("UPDATE_GAME", body);
                }
            }
        )
    }

    @Action({rawError: true})
    public async createGame(): Promise<void> {
        const resp: AxiosResponse = await axios.post(BASE_URL + "/game", {lobbyId: this.context.getters["lobbyId"]});
        if (resp.status === 201) {
            const gameModel: GameCreationModel = resp.data;
            this.context.commit("SET_GAME_ID", gameModel.id);
        }
    }

    @Action({rawError: true})
    public fetchActiveGame(): void {
        websocket.send("/game/fetchGame/" + this.gameId, {});
    }

    @Action({rawError: true})
    public sendCardVote(cardId: number): void {
        const currentPlayerId: number = this.context.getters["currentPlayerId"];
        const cardVoteModel: CardVoteModel = {
            votedCardId: cardId,
            votedPlayerId: currentPlayerId,
        }
        websocket.send(config.GAME_STATE_UPDATE_PATH + "/" + this.game.id, cardVoteModel);
    }

    @Action({commit: "ADD_CARD_VOTE", rawError: true})
    public addCardVote(cardVote: CardVoteModel): CardVoteModel {
        return cardVote;
    }

    @Action({rawError: true})
    public sendPuzzleWord(puzzleWordModel: PuzzleWordModel) {
        websocket.send("/game/setPuzzleWord/" + this.gameId, puzzleWordModel);
    }

    @Action({commit: "SET_GAME_ID", rawError: true})
    public setGameId(gameId: number) {
        return gameId;
    }

    @Action({rawError: true})
    public sendPassVote() {
        const playerId: number = this.context.getters["currentPlayerId"];
        const passedVote: boolean = this.context.getters["currentPlayerPassed"];
        const passedVoteModel: PassedVoteModel = {
            playerId: playerId,
            passed: !passedVote,
        }
        websocket.send("/game/passTurn/" + this.gameId, passedVoteModel);
    }

    @Mutation
    private SET_GAME_ID(gameId: number) {
        this.game.id = gameId;
    }

    @Mutation
    private ADD_CARD_VOTE(cardVote: CardVoteModel): void {
        this.cardVotes.push(cardVote);
    }

    @Mutation
    private UPDATE_GAME(gameStateModel: GameStateModel) {
        this.game = gameStateModel;
    }

    @Mutation
    private SET_GAME(game: GameStateModel): void {
        this.game = game;
    }

    get rounds(): number {
        return this.game.rounds;
    }

    get civiliansFoundByBlueTeam(): number {
        return this.game.civiliansFoundByBlueTeam;
    }

    get civiliansFoundByRedTeam(): number {
        return this.game.civiliansFoundByRedTeam;
    }

    get gameId(): number {
        return this.game.id;
    }

    get board(): Array<CardDetailsModel> {
        return this.game.board;
    }

    get currentTeam(): string {
        return this.game.currentTeam;
    }

    get puzzleWords(): Array<PuzzleWordModel> {
        return this.game.puzzleWords;
    }

    get passVoteCounter(): number {
        return this.game.passVoteCounter;
    }

    get currentTeamSize(): number {
        let teamSize: number;
        const currentTeam = this.game.teams.find(team => team.side === this.game.currentTeam)
        if (currentTeam) {
            teamSize = currentTeam.players.length;
        } else {
            throw new TypeError('The value was promised to always be there!');
        }

        return teamSize;
    }
}