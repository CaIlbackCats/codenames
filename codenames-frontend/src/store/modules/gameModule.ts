import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import * as websocket from '@/services/websocket';
import {config} from "@/config";
import {GameCreationModel} from "@/models/gameCreationModel";

@Module
export default class GameModule extends VuexModule {
    private game: GameCreationModel = {
        id: -1,
        board: [],
        blueScore: 0,
        redScore: 0,
        civiliansFoundByBlueTeam: 0,
        civiliansFoundByRedTeam: 0,
        rounds: 0
    }

    @Action
    public createGame(): void {
        websocket.send(config.CREATE_GAME, {lobbyId: this.context.getters['lobbyId']})
    }

    @Mutation
    private SET_GAME(gameCreationModel: GameCreationModel): void {
        this.game = gameCreationModel;
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

    get blueScore(): number {
        return this.game.blueScore;
    }

    get redScore(): number {
        return this.game.redScore;
    }

    get gameId(): number {
        return this.game.id;
    }
}