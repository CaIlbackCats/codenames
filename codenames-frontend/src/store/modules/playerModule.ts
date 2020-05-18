import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";
import {Message} from "webstomp-client";
import {RoomModel} from "@/models/roomModel";
import {ActionModel} from "@/models/actionModel";
import router from "@/router";
import {PlayerRemovalModel} from "@/models/playerRemovalModel";
import {LobbyModel} from "@/models/lobbyModel";
import {LobbyTeamModel} from "@/models/lobbyTeamModel";


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

    private everyOneRdy = false;

    private initKickWindow = false;

    private blueSpymaster = false;
    private blueSpy = false;
    private redSpymaster = false;
    private redSpy = false;

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
    public subscribeToLobby(roomModel: RoomModel): void {
        const client = roomModel.stompClient;
        client.subscribe(process.env.VUE_APP_OPTIONS + roomModel.name,
            message => {
                if (message.body) {
                    this.context.dispatch("executeLobbyChange", message);
                }
            },
            {id: "lobby"});
    };

    @Action({rawError: true})
    public executeLobbyChange(message: Message): void {
        const messageResult: ActionModel = JSON.parse(message.body);
        switch (messageResult.actionToDo) {
            case "CREATE_PLAYER":
                this.context.commit("ADD_PLAYER", messageResult.currentPlayer)
                break;
            case "UPDATE_LOBBY":
                this.context.commit("UPDATE_LOBBY", messageResult.lobbyDetails)
                break;
            case "UPDATE_PLAYER":
                this.context.commit("UPDATE_PLAYER", messageResult.currentPlayer)
                break;
        }
    }

    @Action({rawError: true})
    public subscribeToPlayerChange(roomModel: RoomModel): void {
        const client = roomModel.stompClient;
        client.subscribe(process.env.VUE_APP_PLAYER_CHANGE + roomModel.name + "/" + roomModel.playerId,
            message => {
                if (message.body) {
                    this.context.dispatch("executePlayerChange", message);
                }
            },
            {
                id: "player",
            });
    };

    @Action
    public executePlayerChange(message: Message): void {
        const messageResult: ActionModel = JSON.parse(message.body);
        switch (messageResult.actionToDo) {
            case "INIT_KICK":
                this.context.dispatch("showKickWindow", true);
                this.context.dispatch("setPlayerRemoval", messageResult.playerRemoval)
                break;
            case "GET_KICKED":
                this.context.dispatch("executeGetKicked");
                break;
            case "UPDATE_PLAYER":
                this.context.dispatch("executePlayerUpdate", messageResult.currentPlayer);
                break;
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

    get getPlayers(): Array<PlayerModel> {
        return this.players;
    }

    get getCurrentPlayer(): PlayerModel {
        return this.currentPlayer;
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
}