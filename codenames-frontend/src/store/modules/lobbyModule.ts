import axios from 'axios';
import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import {LobbyModel} from "@/models/lobby/lobbyModel";
import * as websocket from '@/services/websocket'
import {config} from "@/config";
import {RemainingRoleModel} from "@/models/lobby/remainingRoleModel";
import {PlayerModel} from "@/models/player/playerModel";
import {PlayerDetailsModel} from "@/models/player/playerDetailsModel";
import {LanguageModel} from "@/models/languageModel";

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
        currentGameId: -1,
        kickingPhase: false,

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
        if (response.status === 201) {
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
                // router.push('/')
            }
        }
    }

    get currentGameId(): number {
        return this.lobby.currentGameId;
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
                    this.context.dispatch("updateLobby", body).then(() => {
                        this.context.dispatch("checkRemovablePlayer");
                    });
                }
            });
    }

    @Action({commit: "UPDATE_LOBBY", rawError: true})
    public async updateLobby(lobbyModel: LobbyModel) {
        if (lobbyModel.currentGameId != null) {
            await this.context.dispatch("setGameId", lobbyModel.currentGameId);
        }
        return lobbyModel;
    }

    @Action({rawError: true})
    public hideLeftPlayer(): void {
        const playerDetails: PlayerDetailsModel = {
            id: this.context.getters["currentPlayerId"],
            lobbyName: this.lobbyId,
        }
        websocket.send(config.HIDE_PLAYER_PATH, playerDetails);
    }

    @Action({rawError: true})
    public sendLobbyUpdate(): void {
        websocket.send(config.LOBBY_FETCH_PATH, this.lobby);
    }

    @Action({rawError: true})
    public setGameLanguage(payload: LanguageModel): void {
        axios.post(BASE_URL + "/lobby/" + this.lobbyId, payload);
    }

    get playersOrdered(): Array<PlayerModel> {
        if (this.lobby.players) {
            const currentPlayer: PlayerModel = this.context.getters["player"]
            const currentPlayerIndex: number = this.lobby.players.map(player => player.id).indexOf(currentPlayer.id);
            if (currentPlayerIndex !== -1) {
                const temp: PlayerModel = this.lobby.players[0];
                this.lobby.players[0] = currentPlayer;
                this.lobby.players[currentPlayerIndex] = temp;
            }

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

    get isKickingPhase(): boolean {
        return this.lobby.kickingPhase;
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

    get isEveryoneReady(): boolean {
        return this.lobby.everyoneRdy;
    }

    @Action({rawError: true})
    public async joinLobby(payload: JoinActionPayload): Promise<boolean> {
        const response = await axios.get(`${BASE_URL}/lobby/${payload.lobbyId}`)
        if (response.status === 200) {
            const lobby: LobbyModel = response.data
            this.context.commit('SET_LOBBY', lobby)
            await this.context.dispatch("subscribeToLobby");
            await this.context.dispatch("checkSelectedPlayer", {root: true});
            return true;
        }
        return false;
    }
}