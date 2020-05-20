import axios from 'axios';
import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import {LobbyModel} from "@/models/lobbyModel";
import router from "@/router";
import * as websocket from '@/services/websocket'
import {config} from "@/config";

const BASE_URL = process.env.VUE_APP_BASE_URL;

interface JoinActionPayload {
    lobbyId: string
}

@Module
export default class LobbyModule extends VuexModule {
    lobby: LobbyModel = {
        id: "",
        players: [],
        everyOneRdy: false,
        redSpy: false,
        blueSpy: false,
        redSpymaster: false,
        blueSpymaster: false,
    }

    @Mutation
    private SET_LOBBY(lobbyModel: LobbyModel): void {
        this.lobby = lobbyModel
    }

    @Action
    public async createLobby(): Promise<boolean> {
        const response = await axios.post(BASE_URL + "/lobby")
        if (response.status === 200) {
            const lobby: LobbyModel = response.data
            this.context.commit('SET_LOBBY', lobby)
            return true;
        }
        return false;
    }

    @Action({rawError: true})
    public async joinLobby(payload: JoinActionPayload): Promise<boolean> {
        // check that the lobby exists
        const response = await axios.get(`${BASE_URL}/lobby/${payload.lobbyId}`)
        if (response.status === 200) {
            const lobby: LobbyModel = response.data
            this.context.commit('SET_LOBBY', lobby)
            // subscribe player to lobby
           await this.context.dispatch("subscribeToLobby");
           await this.context.dispatch("checkSelectedPlayer", {root: true});
            return true;
        }
        return false;

    }

    @Action({rawError: true})
    public async subscribeToLobby() : Promise<void> {
      await websocket.subscribe(
            `${config.wsLobbyPath}${this.lobby.id}`,
            body => {
                if (body) {
                    this.context.dispatch("executeLobbyChange", body, {root: true});
                }
            });
    }

    get lobbyId(): string {
        return this.lobby.id;
    }
}