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
    lobby?: LobbyModel

    @Mutation
    private SET_LOBBY(lobbyModel: LobbyModel): void {
        this.lobby = lobbyModel
    }

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
    public async joinLobby(payload: JoinActionPayload): Promise<void> {
        try {
            // check that the lobby exists
            const response = await axios.get(`${BASE_URL}/lobby/${payload.lobbyId}`)
            if (response.status === 200) {
                const lobby: LobbyModel = response.data
                this.context.commit('SET_LOBBY', lobby)

                // subscribe player to lobby
                await websocket.subscribe(
                    `${config.wsLobbyPath}${payload.lobbyId}`,
                    body => {
                        if (body) this.context.dispatch("executeLobbyChange", body, {root: true});
                    }
                    ,
                    {id: "lobby"}
                );
                this.context.dispatch("checkSelectedPlayer", {lobbyId: payload.lobbyId})
            } else {
                router.push('/')
            }
        } catch (err) {
            router.push('/')
        }
    }
}