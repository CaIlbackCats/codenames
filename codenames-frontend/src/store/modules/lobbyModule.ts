import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {LobbyModel} from "@/models/lobbyModel";
import router from "@/router";
import axios from 'axios';

const BASE_URL = process.env.VUE_APP_BASE_URL;

@Module
export default class LobbyModule extends VuexModule {
    lobby?: LobbyModel

    @Action
    public async createLobby(): Promise<void> {
        const response = await axios.post(BASE_URL + "/lobby")
        if (response.status === 200) {
            const lobby: LobbyModel = response.data
            // this.context.commit('SET_LOBBY', lobby)
            router.push({name: "Lobby", params: {lobbyId: lobby.id}});
        }
    }

    @Action
    public async joinLobby(id: string): Promise<void> {
        try {
            // check that the lobby exists
            const response = await axios.get(`${BASE_URL}/lobby/${id}`)
            if (response.status === 200) {
                const lobby: LobbyModel = response.data
                this.context.commit('SET_LOBBY', lobby)
            } else {
                router.push('/')
            }
            // TODO: register a new player etc.
        } catch (err) {
            router.push('/')
        }
    }

    @Mutation
    private SET_LOBBY(lobbyModel: LobbyModel): void {
        this.lobby = lobbyModel
    }

    // @Action({commit: 'setLobby'})
    // public async joinLobby(id: string) {
    //     // update the backend, registering a new player
    //     return id
    // }
}