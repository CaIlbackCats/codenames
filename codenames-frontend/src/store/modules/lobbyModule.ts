import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {LobbyModel} from "@/models/lobbyModel";
import router from "@/router";
import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";

@Module
export default class LobbyModule extends VuexModule {
    lobby?: LobbyModel

    // @Mutation
    // private setLobby(id: string) {
    //     this.lobby = { id }
    // }

    // @Action
    // public async createLobby() {
    //     // hhit backend and get back the ID of the lobby (async)
    //     const id = createUUID()
    //     this.context.commit('setLobby', id)
    //     return router.push({name: "Lobby", params: {lobbyId: id}})
    // }

    @Action({commit: "CREATE_LOBBY", rawError: true})
    public async createLobby(): Promise<string> {
        const response = await axios.get(BASE_URL + "/lobby").then((response) => {
            this.context.commit('CREATE_LOBBY', response.data)
            console.log(response);
        });
        return "";
    }

    @Mutation
    private CREATE_LOBBY(lobbyModel: LobbyModel):void{
        this.lobby = lobbyModel;
        router.push({name: "Lobby", params: {lobbyId: lobbyModel.id}});
    }

    @Action({commit: 'setLobby'})
    public async joinLobby(id: string) {
        // update the backend, registering a new player
        return id
    }
}