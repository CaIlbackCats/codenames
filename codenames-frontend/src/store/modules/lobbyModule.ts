import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {LobbyModel} from "@/models/lobbyModel";
import router from "@/router";
import axios from 'axios';

const BASE_URL = process.env.VUE_APP_BASE_URL;

@Module
export default class LobbyModule extends VuexModule {
    lobby?: LobbyModel

    @Action({commit: "CREATE_LOBBY", rawError: true})
    public async createLobby(): Promise<string> {
        const response = await axios.post(BASE_URL + "/lobby")
        return response.data;
    }

    @Mutation
    private CREATE_LOBBY(lobbyModel: LobbyModel): void {
        this.lobby = lobbyModel;
        router.push({name: "Lobby", params: {lobbyId: lobbyModel.id}});
    }

    // @Action({commit: 'setLobby'})
    // public async joinLobby(id: string) {
    //     // update the backend, registering a new player
    //     return id
    // }
}