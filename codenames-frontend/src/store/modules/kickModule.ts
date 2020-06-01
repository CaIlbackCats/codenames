import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/player/playerModel";
import {PlayerRemovalModel} from "@/models/player/kick/playerRemovalModel";
import * as websocket from '@/services/websocket'
import {config} from "@/config";


@Module
export default class KickModule extends VuexModule {

    private playerToKick: PlayerModel = {
        id: -1,
        name: "",
        role: "",
        side: "",
        rdyState: false,
        lobbyOwner: false,
        passed: false,
    }

    private showKickWindow = false;

    private playerRemovalModel!: PlayerRemovalModel;


    @Mutation
    private SET_KICK_WINDOW(show: boolean) {
        this.showKickWindow = show;
    }

    @Mutation
    private SET_PLAYER_TO_KICK(playerModel: PlayerModel) {
        this.playerToKick = playerModel;
    }

    @Mutation
    private SET_PLAYER_REMOVAL_MODEL(playerRemovalModel: PlayerRemovalModel) {
        this.playerRemovalModel = playerRemovalModel;
    }

    @Action({rawError: true})
    public subscribeToKick(): void {
        const lobbyId: string = this.context.getters["lobbyId"];
        const kickPath = config.LOBBY_SUBSCRIPTION_PATH + lobbyId + config.LOBBY_KICK_SUBSCRIPTION_PATH;
        websocket.subscribe(kickPath, (body) => {
            if (body) {
                this.context.dispatch("updatePlayerRemovalInfo", body)
            }
        });
    }

    @Action({rawError: true})
    public sendKickWindowInit(playerToRemoveId: number): void {
        const ownerId: number = this.context.getters["currentPlayerId"];

        const playerRemovalModel: PlayerRemovalModel = {
            ownerId: ownerId,
            playerToRemoveId: playerToRemoveId,
        }
        websocket.send(config.PLAYER_INIT_KICK_PATH, playerRemovalModel);
    }

    @Action({rawError: true})
    public updatePlayerRemovalInfo(playerRemovalModel: PlayerRemovalModel): void {
        const currentId: number = this.context.getters["currentPlayerId"];
        const showWindow: boolean = currentId != playerRemovalModel.playerToRemoveId;

        this.context.commit("SET_PLAYER_REMOVAL_MODEL", playerRemovalModel);
        this.context.commit("SET_KICK_WINDOW", showWindow);
        this.context.commit("SET_PLAYER_TO_KICK", playerRemovalModel.playerToRemove);
    }

    @Action({commit: "SET_KICK_WINDOW", rawError: true})
    public setKickWindow(show: boolean) {
        return show;
    }

    @Action({rawError: true})
    public sendVote(vote: boolean): void {
        this.playerRemovalModel.vote = vote;
        websocket.send(config.PLAYER_COUNT_KICKS_PATH, this.playerRemovalModel);
    }

    @Action({rawError: true})
    public getPlayersInLobby() {
        websocket.send(config.LOBBY_GET_PLAYERS_PATH, this.playerRemovalModel);
    }


    get playerToKickId(): number {
        return this.playerToKick.id;
    }

    get isKickWindow(): boolean {
        return this.showKickWindow;
    }

    get playerToKickName(): string {
        return this.playerToKick.name;
    }

}