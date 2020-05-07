import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";
import axios from "axios";

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

    @Action({commit: "ADD_NEW_PLAYER", rawError: true})
    public async addNewPlayer(name: string): Promise<PlayerModel> {
        const response = await axios.post<PlayerModel>(PLAYERS_ENDPOINT, name, AXIOS_CONFIG);
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
        const response = await axios.get<Array<PlayerModel>>(PLAYERS_ENDPOINT, AXIOS_CONFIG);
        return response.data
    }

    get getPlayers() {
        return this.players;
    }
}