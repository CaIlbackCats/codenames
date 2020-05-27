import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import * as websocket from '@/services/websocket'
import {PlayerModel} from "@/models/playerModel";
import {PlayerDetailsModel} from "@/models/playerDetailsModel";
import {config} from "@/config";
import {RdyModel} from "@/models/rdyModel";
import {SelectionModel} from "@/models/selectionModel";
import {PlayerCreationModel} from "@/models/playerCreationModel";
import axios from 'axios';
import {LobbyModel} from "@/models/lobbyModel";


@Module
export default class PlayerModule extends VuexModule {


    private currentPlayer: PlayerModel = {
        id: -1,
        name: "",
        lobbyOwner: false,
        role: "",
        side: "",
        rdyState: false,
    };

    private playerSelected = false;


    @Mutation
    private ADD_PLAYER(player: PlayerModel): void {
        if (this.currentPlayer.id === -1) {
            this.currentPlayer = player;
        }
    }

    @Mutation
    private REMOVE_CURRENT_PLAYER(): void {
        this.currentPlayer = {
            id: -1,
            role: "",
            side: "",
            rdyState: false,
            name: "",
            lobbyOwner: false,
        }
        this.playerSelected = false;

    }

    @Mutation
    private UPDATE_PLAYER(playerModel: PlayerModel): void {
        this.currentPlayer = playerModel;
    }
    @Action({rawError: true})
    public subscribeToPlayerChange(): void {
        const lobbyId: string = this.context.getters["lobbyId"];
        websocket.subscribe(config.PLAYER_SUBSCRIPTION_PATH + lobbyId + "/" + this.currentPlayer.id,
            messageBody => {
                if (messageBody) {
                    this.context.commit("UPDATE_PLAYER", messageBody);
                }
            });
    };

    @Action({rawError: true})
    public checkSelectedPlayer() {
        const currentPlayerId = localStorage.getItem('currentPlayerId');
        if (currentPlayerId) {
            this.context.commit("SET_PLAYER_SELECTED", true);
            const existingPlayer: PlayerDetailsModel = {
                lobbyName: this.context.getters["lobbyId"],
                id: Number(currentPlayerId),
            }
            websocket.send(config.PLAYER_FETCH_PATH, existingPlayer);
        }
    }

    @Action({rawError: true})
    public sendReadyState(): void {
        const readyModel: RdyModel = {
            playerId: this.currentPlayer.id,
            rdyState: !this.currentPlayer.rdyState,
        }
        websocket.send(config.PLAYER_SET_READY_PATH, readyModel);
    }

    @Action({rawError: true})
    public sendSelection(selectionModel: SelectionModel): void {

        websocket.send(config.PLAYER_ROLE_SELECTION_PATH, selectionModel);
    }

    @Action({rawError: true})
    public async sendPlayerCreation(playerCreationModel: PlayerCreationModel) {
        const resp = await axios.post(process.env.VUE_APP_BASE_URL + "/createPlayer", playerCreationModel);
        if (resp.status === 201) {
            const player: PlayerModel = resp.data;
            this.context.commit("UPDATE_PLAYER", player);

        }
        const lobbyModel: LobbyModel = {
            id: playerCreationModel.lobbyName,
            everyoneRdy: false,
            players: [],
            currentGameId:-1
        }
        websocket.send(config.LOBBY_FETCH_PATH, lobbyModel);
        //  websocket.send(config.LOBBY_CREATE_PLAYER_PATH, playerCreationModel);
    }

    get isRoleSelected(): boolean {
        return this.currentPlayer.role === "NOT_SELECTED";
    }

    get player(): PlayerModel {
        return this.currentPlayer;
    }

    get isCurrentPlayerOwner(): boolean {
        return this.currentPlayer.lobbyOwner;
    }

    get currentPlayerId(): number {
        return this.currentPlayer.id;
    }

    get currentPlayerName(): string {
        return this.currentPlayer.name;
    }

    get currentPlayerRole(): string {
        return this.currentPlayer.role;
    }

    get currentPlayerSide(): string {
        return this.currentPlayer.side;
    }

    get isPlayerSelected(): boolean {
        return this.currentPlayer.id !== -1;
    }
}