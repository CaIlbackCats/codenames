import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import * as websocket from '@/services/websocket';
import {config} from "@/config";
import {GameCreationModel} from "@/models/gameCreationModel";

@Module
export default class GameModule extends VuexModule {
    private game: GameCreationModel = {
        id: -1,
        board: []
    }

    @Action
    public createGame(): void {
        websocket.send(config.CREATE_GAME, {lobbyId: this.context.getters['lobbyId']})
    }

    @Mutation
    private SET_GAME(gameCreationModel: GameCreationModel): void {
        this.game = gameCreationModel;
    }

    get gameId(): number {
        return this.game.id;
    }
}