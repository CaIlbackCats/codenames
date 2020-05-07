import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {LobbyModel} from "@/models/lobbyModel";
import router from "@/router";

const createUUID = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return r.toString(16);
    });
}

@Module
export default class LobbyModule extends VuexModule {
    lobby?: LobbyModel

    @Mutation
    private setLobby(id: string) {
        this.lobby = { id }
    }

    @Action
    public async createLobby() {
        // hhit backend and get back the ID of the lobby (async)
        const id = createUUID()
        this.context.commit('setLobby', id)
        return router.push({name: "Lobby", params: {lobbyId: id}})
    }

    @Action({commit: 'setLobby'})
    public async joinLobby(id: string) {
        // update the backend, registering a new player
        return id
    }
}