import { Action, Module, Mutation, VuexModule } from "vuex-module-decorators";
import * as websocket from "@/services/websocket";
import { config } from "@/config";

@Module
export default class OptionModule extends VuexModule {
  @Action({ rawError: true })
  public sendRandomizeRole(): void {
    const lobbyId: string = this.context.getters["lobbyId"];
    websocket.send(config.LOBBY_ROLE_PATH, lobbyId);
  }

  @Action({ rawError: true })
  public sendRandomizeSide(): void {
    const lobbyId: string = this.context.getters["lobbyId"];
    websocket.send(config.LOBBY_SIDE_PATH, lobbyId);
  }
}
