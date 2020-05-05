import {Module, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";

@Module
export default class PlayerModule extends VuexModule {

    players: Array<PlayerModel> = [];

    activePlayer: PlayerModel = {
        lobbyOwner: false,
        name: "",
        role: "",
    }

    get getPlayers(): Array<PlayerModel> {
        return this.players;
    }

    get getActivePlayer(): PlayerModel {
        return this.activePlayer;
    }

}