import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";
import {Message} from "webstomp-client";
import {RoomModel} from "@/models/roomModel";
import {ActionModel} from "@/models/actionModel";
import router from "@/router";


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

    private initKickWindow = false;

    @Mutation
    private REFRESH_LIST(player: Message): void {
        this.players = JSON.parse(player.body);
    }

    @Mutation
    private SET_CURRENT_PLAYER(player: PlayerModel): void {
        this.currentPlayer = player;
    }

    @Mutation
    private SHOW_KICK_WINDOW(show: boolean): void {
        this.initKickWindow = show;
    }

    @Mutation
    private REMOVE_CURRENT_PLAYER() {
        this.currentPlayer = {
            id: -1,
            name: "",
            lobbyOwner: false,
            role: "",
            side: "",
        }
        router.push({name: "Home"});

    }

    @Action({rawError: true})
    public subscribeToOptions(roomModel: RoomModel): void {
        const client = roomModel.stompClient;
        client.subscribe(process.env.VUE_APP_OPTIONS + roomModel.name, message => {
            if (message.body) {
                this.context.commit("REFRESH_LIST", message);
            }
        });
    }

    @Action({rawError: true})
    public subscribeToPlayerChange(roomModel: RoomModel): void {
        const client = roomModel.stompClient;
        client.subscribe(process.env.VUE_APP_PLAYER_CHANGE + roomModel.playerName, message => {
            if (message.body) {
                this.context.dispatch("executePlayerChange", message);
            }
        });
    };

    @Action
    public executePlayerChange(message: Message): void {
        const messageResult: ActionModel = JSON.parse(message.body);
        switch (messageResult.actionToDo) {
            case "SET_CURRENT_PLAYER":
                this.context.dispatch("executeSetCurrentPlayer", messageResult);
                break;
            case "INIT_KICK":
                this.context.dispatch("showKickWindow", true);
                break;
            case "GET_KICKED":
                this.context.dispatch("executeGetKicked");
                break;
        }
    }

    @Action({commit: "SHOW_KICK_WINDOW", rawError: true})
    public showKickWindow(show: boolean) {
        return show;
    }

    @Action({commit: "SET_CURRENT_PLAYER", rawError: true})
    public executeSetCurrentPlayer(messageResult: ActionModel): PlayerModel {
        return messageResult.currentPlayer;
    }

    @Action({commit: "REMOVE_CURRENT_PLAYER"})
    public executeGetKicked() {
        return true;
    }

    get getPlayers() {
        return this.players;
    }

    get getCurrentPlayer() {
        return this.currentPlayer;
    }

    get getInitKickWindow() {
        return this.initKickWindow;
    }
}