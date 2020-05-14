import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";
import {Message} from "webstomp-client";
import {RoomModel} from "@/models/roomModel";
import {ActionModel} from "@/models/actionModel";
import router from "@/router";
import {PlayerRemovalModel} from "@/models/playerRemovalModel";


@Module
export default class PlayerModule extends VuexModule {

    private players: Array<PlayerModel> = [];

    private currentPlayer: PlayerModel = {
        id: -1,
        name: "",
        lobbyOwner: false,
        role: "",
        side: "",
    };

    private playerRemovalInfo: PlayerRemovalModel = {
        ownerId: -1,
        vote: false,
        playerToRemoveId: -1,
        kickType: "",
    };

    private initKickWindow = false;

    @Mutation
    private ADD_PLAYER(player: PlayerModel): void {
        console.log("------------------------")
        console.log(this.currentPlayer)
        if (this.currentPlayer.id === -1) {
            this.currentPlayer = player;
        }
    }

    @Mutation
    private SHOW_KICK_WINDOW(show: boolean): void {
        this.initKickWindow = show;
    }

    @Mutation
    private UPDATE_LIST(players: Array<PlayerModel>): void {
        this.players = players;
    }

    @Mutation
    private REMOVE_CURRENT_PLAYER(): void {
        this.currentPlayer = {
            id: -1,
            name: "",
            lobbyOwner: false,
            role: "",
            side: "",
        }
        console.log("------------------------")
        console.log(this.currentPlayer)
        router.push({name: "Home"});
    }

    @Mutation
    private SET_PLAYER_REMOVAL(playerRemoval: PlayerRemovalModel): void {
        this.playerRemovalInfo = playerRemoval;
    }

    @Mutation
    private UPDATE_PLAYER_REMOVAL_INFO(playerRemovalModel: PlayerRemovalModel): void {
        this.playerRemovalInfo.ownerId = playerRemovalModel.ownerId;
        this.playerRemovalInfo.playerToRemoveId = playerRemovalModel.playerToRemoveId;
        this.playerRemovalInfo.vote = playerRemovalModel.vote;
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
            case "UPDATE_LIST":
                this.context.commit("UPDATE_LIST", messageResult.playerList)
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
        }
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

    get getPlayers() {
        return this.players;
    }

    get getCurrentPlayer() {
        return this.currentPlayer;
    }

    get getInitKickWindow() {
        return this.initKickWindow;
    }

    get getPlayerRemovalInfo() {
        return this.playerRemovalInfo;
    }
}