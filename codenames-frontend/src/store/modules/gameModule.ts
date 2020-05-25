import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import * as websocket from '@/services/websocket';
import {config} from "@/config";
import {GameCreationModel} from "@/models/gameCreationModel";
import {GameStateModel} from "@/models/gameStateModel";
import {TeamVoteModel} from "@/models/teamVoteModel";

@Module
export default class GameModule extends VuexModule {
    private game: GameStateModel = {
        id: -1,
        board: [],
        blueScore: 0,
        redScore: 0,
        endGame: false,
        endTurn: false,
        winnerTeam: "",
        gameEndByAssassin: false,
        startingTeamColor: "",
    }

    @Action({rawError: true})
    public subscribeToGame() {
        const lobbyId: string = this.context.getters["lobbyId"];
        const gamePath: string = config.LOBBY_SUBSCRIPTION_PATH + lobbyId + "/" + this.gameId;
        websocket.subscribe(gamePath, (body) => {
                if (body) {
                    this.context.commit("UPDATE_GAME", body);
                }
            }
        )
    }

    @Action({rawError: true})
    public createGame(): void {
        websocket.send(config.CREATE_GAME_PATH, {lobbyId: this.context.getters['lobbyId']})
    }

    @Action({rawError: true})
    public sendGameState(teamVoteModel: TeamVoteModel): void {
        websocket.send(config.GAME_STATE_UPDATE_PATH, teamVoteModel);
    }

    @Mutation
    private UPDATE_GAME(gameStateModel: GameStateModel) {
        this.game = gameStateModel;
    }

    @Mutation
    private SET_GAME(gameCreationModel: GameCreationModel): void {
        this.game.id = gameCreationModel.id;
        this.game.board = gameCreationModel.board;
    }

    get gameId(): number {
        return this.game.id;
    }
}