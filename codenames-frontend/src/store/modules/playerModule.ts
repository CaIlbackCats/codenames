import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import router from "@/router";
import * as websocket from '@/services/websocket'
import {PlayerModel} from "@/models/playerModel";
import {RoomModel} from "@/models/roomModel";
import {ActionModel} from "@/models/actionModel";
import {PlayerRemovalModel} from "@/models/playerRemovalModel";
import {LobbyModel} from "@/models/lobbyModel";
import {LobbyTeamModel} from "@/models/lobbyTeamModel";
import {PlayerDetailsModel} from "@/models/playerDetailsModel";
import {config} from "@/config";


interface CheckSelectedPlayerActionPayload {
    lobbyId: string;
}

@Module
export default class PlayerModule extends VuexModule {

    private players: Array<PlayerModel> = [];

    private currentPlayer: PlayerModel = {
        id: -1,
        name: "",
        lobbyOwner: false,
        role: "",
        side: "",
        rdyState: false,
    };

    private playerRemovalInfo: PlayerRemovalModel = {
        ownerId: -1,
        vote: false,
        playerToRemoveId: -1,
        kickType: "",
    };

    private isPlayerSelected = false;


    private everyOneRdy = false;

    private initKickWindow = false;

    private blueSpymaster = false;
    private blueSpy = false;
    private redSpymaster = false;
    private redSpy = false;

    @Mutation
    private SET_PLAYER_SELECTED(isPlayerSelected: boolean) {
        this.isPlayerSelected = isPlayerSelected;
    }

    @Mutation
    private ADD_PLAYER(player: PlayerModel): void {
        if (this.currentPlayer.id === -1) {
            this.currentPlayer = player;
        }
    }

    @Mutation
    private SHOW_KICK_WINDOW(show: boolean): void {
        this.initKickWindow = show;
    }

    @Mutation
    private UPDATE_LOBBY(lobbyModel: LobbyModel): void {
        this.players = lobbyModel.players;
        this.everyOneRdy = lobbyModel.everyOneRdy;
        this.blueSpymaster = lobbyModel.blueSpymaster;
        this.blueSpy = lobbyModel.blueSpy;
        this.redSpymaster = lobbyModel.redSpymaster;
        this.redSpy = lobbyModel.redSpy;

    }

    @Mutation
    private REMOVE_CURRENT_PLAYER(): void {
        this.currentPlayer = {
            id: -1,
            name: "",
            lobbyOwner: false,
            role: "",
            side: "",
            rdyState: false,
        }
        router.push({name: "Home"});
    }

    @Mutation
    private SET_PLAYER_REMOVAL(playerRemoval: PlayerRemovalModel): void {
        this.playerRemovalInfo = playerRemoval;
    }

    @Mutation
    private UPDATE_PLAYER_REMOVAL_INFO(playerRemovalModel: PlayerRemovalModel): void {
        if (this.playerRemovalInfo.ownerId == -1) {
            this.playerRemovalInfo.ownerId = playerRemovalModel.ownerId;
        }
        if (this.playerRemovalInfo.playerToRemoveId == -1) {
            this.playerRemovalInfo.playerToRemoveId = playerRemovalModel.playerToRemoveId;
        }
        this.playerRemovalInfo.vote = playerRemovalModel.vote;
    }

    @Mutation
    private UPDATE_PLAYER(playerModel: PlayerModel): void {
        this.currentPlayer = playerModel;
    }

    @Mutation
    private UPDATE_LOBBY_TEAM(lobbyTeam: LobbyTeamModel): void {
        this.blueSpymaster = lobbyTeam.blueSpymaster;
        this.blueSpy = lobbyTeam.blueSpy;
        this.redSpymaster = lobbyTeam.redSpymaster;
        this.redSpy = lobbyTeam.redSpy;
    }

    @Action({rawError: true})
    public executeLobbyChange(messageBody: ActionModel): void {
        switch (messageBody.actionToDo) {
            case "CREATE_PLAYER":
                this.context.commit("ADD_PLAYER", messageBody.currentPlayer)
                break;
            case "UPDATE_LOBBY":
                this.context.commit("UPDATE_LOBBY", messageBody.lobbyDetails)
                break;
            case "SET_PREVIOUS_PLAYER":
                this.context.commit("UPDATE_PLAYER", messageBody.currentPlayer)
                break;
            case "DELETE_PREV_PLAYER":
                this.context.commit("SET_PLAYER_SELECTED", false)
                break;
            case "CREATE_GAME":
                this.context.commit("SET_GAME", messageBody.gameDetails)
                break;
        }
    }

    @Action({rawError: true})
    public subscribeToPlayerChange(): void {
        const lobbyId: string = this.context.getters["lobbyId"];
        websocket.subscribe(config.PLAYER_SUBSCRIPTION_PATH + lobbyId + "/" + this.currentPlayer.id,
            messageBody => {
                if (messageBody) {
                    this.context.dispatch("executePlayerChange", messageBody);
                }
            });
    };

    @Action
    public executePlayerChange(messageBody: ActionModel): void {
        switch (messageBody.actionToDo) {
            case "INIT_KICK":
                this.context.dispatch("showKickWindow", true);
                this.context.dispatch("setPlayerRemoval", messageBody.playerRemoval)
                break;
            case "GET_KICKED":
                this.context.dispatch("executeGetKicked");
                break;
            case "UPDATE_PLAYER":
                this.context.dispatch("executePlayerUpdate", messageBody.currentPlayer);
                break;
        }
    }

    @Action({rawError: true})
    public checkSelectedPlayer(payload: CheckSelectedPlayerActionPayload) {
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

    @Action({commit: "UPDATE_PLAYER", rawError: true})
    public executePlayerUpdate(playerModel: PlayerModel) {
        return playerModel;
    }

    @Action({commit: "SHOW_KICK_WINDOW", rawError: true})
    public showKickWindow(show: boolean): boolean {
        return show;
    }

    @Action({commit: "REMOVE_CURRENT_PLAYER"})
    public executeGetKicked(): boolean {
        return true;
    }

    @Action({commit: "SET_PLAYER_REMOVAL", rawError: true})
    public setPlayerRemoval(playerRemoval: PlayerRemovalModel): PlayerRemovalModel {
        return playerRemoval;
    }

    @Action({commit: "UPDATE_PLAYER_REMOVAL_INFO", rawError: true})
    public updatePlayerRemovalInfo(playerRemovalModel: PlayerRemovalModel): PlayerRemovalModel {
        return playerRemovalModel;
    };

    get playersOrdered(): Array<PlayerModel> {
        const currentPlayerIndex: number = this.players.map(player => player.id).indexOf(this.currentPlayer.id);
        const temp: PlayerModel = this.players[0];
        this.players[0] = this.currentPlayer;
        this.players[currentPlayerIndex] = temp;

        return this.players;
    }

    get getCurrentPlayer(): PlayerModel {
        return this.currentPlayer;
    }

    get currentPlayerId(): number {
        return this.currentPlayer.id;
    }

    get partySize(): number {
        return this.players.length;
    }

    get getInitKickWindow(): boolean {
        return this.initKickWindow;
    }

    get getPlayerRemovalInfo(): PlayerRemovalModel {
        return this.playerRemovalInfo;
    }

    get isEveryOneRdy(): boolean {
        return this.everyOneRdy;
    }

    get isBlueSpymasterFull(): boolean {
        return this.blueSpymaster;
    }

    get isBlueSpyFull(): boolean {
        return this.blueSpy;
    }

    get isRedSpymasterFull(): boolean {
        return this.redSpymaster;
    }

    get isRedSpyFull(): boolean {
        return this.redSpy;
    }

    get getPlayerSelected(): boolean {
        return this.isPlayerSelected;
    }
}