import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";
import axios from "axios";
import {PlayerCreationModel} from "@/models/playerCreationModel";
import {Message} from "webstomp-client";
import {RoomModel} from "@/models/roomModel";

const AXIOS_CONFIG = {headers: {'Content-Type': 'application/json',}};
const PLAYERS_ENDPOINT = process.env.VUE_APP_BASE_URL + "/players";

@Module
export default class PlayerModule extends VuexModule {

    private players: Array<PlayerModel> = [];

    @Mutation
    private ADD_NEW_PLAYER(player: PlayerModel): void {
        this.players.push(player);
    }

    @Mutation
    private REFRESH_LIST(players: Array<PlayerModel>): void {
        this.players = players;
    }

    @Mutation
    private REFRESH_LIST2(player: Message): void {
        this.players = JSON.parse(player.body);
    }

    @Action({rawError:true})
    private subscribeToOptions(roomModel: RoomModel) {
        const client = roomModel.stompClient;
        client.subscribe(process.env.VUE_APP_OPTIONS + roomModel.name, message => {
            if (message.body) {
                this.context.commit("REFRESH_LIST2", message);
            }
        });
    }

    @Action({commit: "ADD_NEW_PLAYER", rawError: true})
    public async addNewPlayer(player: PlayerCreationModel): Promise<PlayerModel> {
        const response = await axios.post<PlayerModel>(PLAYERS_ENDPOINT, player, AXIOS_CONFIG);
        return response.data;
    }

    @Action({commit: "REFRESH_LIST", rawError: true})
    public async setPlayerRole(lobbyName: string): Promise<Array<PlayerModel>> {
        const response = await axios.put<Array<PlayerModel>>(PLAYERS_ENDPOINT + "/role", lobbyName, AXIOS_CONFIG);
        return response.data;
    }

    @Action({commit: "REFRESH_LIST", rawError: true})
    public async setPlayerSide(lobbyName: string): Promise<Array<PlayerModel>> {
        const response = await axios.put<Array<PlayerModel>>(PLAYERS_ENDPOINT + "/side", lobbyName, AXIOS_CONFIG);
        return response.data;
    }

    @Action({commit: "REFRESH_LIST", rawError: true})
    public async fetchPlayers(lobbyName: string): Promise<Array<PlayerModel>> {
        const response = await axios.get<Array<PlayerModel>>(PLAYERS_ENDPOINT + "?lobbyName=" + lobbyName, AXIOS_CONFIG);
        return response.data
    }

    get getPlayers() {
        return this.players;
    }
}