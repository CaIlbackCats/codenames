import axios from 'axios';
import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import {LobbyModel} from "@/models/lobbyModel";
import * as websocket from '@/services/websocket'
import {config} from "@/config";
import {LobbyOptionsModel} from "@/models/lobbyOptionsModel";
import {RemainingRoleModel} from "@/models/remainingRoleModel";
import router from "@/router";
import {PlayerModel} from "@/models/playerModel";

const BASE_URL = process.env.VUE_APP_BASE_URL;

interface JoinActionPayload {
    lobbyId: string
}

@Module
export default class LobbyModule extends VuexModule {

    lobby: LobbyModel = {
        id: "",
        players: [],
        everyoneRdy: false,
    }
    private remainingRoleModel: RemainingRoleModel = {
        blueSpy: false,
        blueSpymaster: false,
        redSpy: false,
        redSpymaster: false,
    }

    @Mutation
    private SET_LOBBY(lobbyModel: LobbyModel): void {
        this.lobby = lobbyModel
    }

    @Mutation
    private SET_REMAINING_ROLE_DATA(remainingRoles: RemainingRoleModel) {
        this.remainingRoleModel = remainingRoles;
    }

    @Mutation
    private UPDATE_LOBBY(lobbyModel: LobbyModel): void {
        this.lobby = lobbyModel;
    }

    @Action({rawError: true})
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
    public checkRemovablePlayer(): void {
        const currentPlayerId: number = this.context.getters["currentPlayerId"];
        if (this.lobby.players) {
            const playersContainCurrent: Array<number> = this.lobby.players.map(player => player.id).filter(id => id === currentPlayerId)
            if (playersContainCurrent.length === 0) {
                this.context.commit("REMOVE_CURRENT_PLAYER")
                router.push('/')
            }
        }
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
            await this.context.dispatch("subscribeToLobbyRoleData");
            this.context.dispatch("checkRemovablePlayer");
            // await this.context.dispatch("checkSelectedPlayer", {root: true});
            return true;
        }
        return false;
    }


    @Action({rawError: true})
    public subscribeToLobbyRoleData(): void {
        websocket.subscribe(config.LOBBY_SUBSCRIPTION_PATH + this.lobby.id + config.LOBBY_ROLE_DATA_SUBSCRIPTION_PATH, (body) => {
            if (body) {
                this.context.commit("SET_REMAINING_ROLE_DATA", body);
            }
        });
    }

    @Action({rawError: true})
    public async subscribeToLobby(): Promise<void> {
        await websocket.subscribe(
            `${config.LOBBY_SUBSCRIPTION_PATH}${this.lobby.id}`,
            body => {
                if (body) {
                    this.context.commit("UPDATE_LOBBY", body);
                }
            });
    }

    @Action({rawError: true})
    public sendInitLobbyRequest() {
        console.log("")
    }


    get playersOrdered(): Array<PlayerModel> {
        if (this.lobby.players) {
            const currentPlayer: PlayerModel = this.context.getters["player"]
            const currentPlayerIndex: number = this.lobby.players.map(player => player.id).indexOf(currentPlayer.id);
            const temp: PlayerModel = this.lobby.players[0];
            this.lobby.players[0] = currentPlayer;
            this.lobby.players[currentPlayerIndex] = temp;
        }

        return this.lobby.players;
    }

    get partySize(): number {
        if (this.lobby.players) {
            return this.lobby.players.length;
        } else {
            return -1;
        }
    }

    get lobbyId(): string {
        return this.lobby.id;
    }

    get isBlueSpyFull(): boolean {
        return this.remainingRoleModel.blueSpy;
    }

    get isBlueSpymasterFull(): boolean {
        return this.remainingRoleModel.blueSpymaster;
    }

    get isRedSpyFull(): boolean {
        return this.remainingRoleModel.redSpy;
    }

    get isRedSpymasterFull(): boolean {
        return this.remainingRoleModel.redSpymaster;
    }
}