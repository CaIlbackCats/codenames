import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";
import {PlayerModel} from "@/models/playerModel";
import {Message} from "webstomp-client";
import {RoomModel} from "@/models/roomModel";


@Module
export default class PlayerModule extends VuexModule {

    private players: Array<PlayerModel> = [];

    @Mutation
    private ADD_NEW_PLAYER(player: PlayerModel): void {
        this.players.push(player);
    }

    @Mutation
    private REFRESH_LIST(player: Message): void {
        this.players = JSON.parse(player.body);
    }

    @Action({rawError:true})
    private subscribeToOptions(roomModel: RoomModel) {
        const client = roomModel.stompClient;
        client.subscribe(process.env.VUE_APP_OPTIONS + roomModel.name, message => {
            if (message.body) {
                this.context.commit("REFRESH_LIST", message);
            }
        });
    }

    get getPlayers() {
        return this.players;
    }
}