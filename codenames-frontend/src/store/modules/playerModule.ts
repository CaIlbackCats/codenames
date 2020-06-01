import { Action, Module, Mutation, VuexModule } from "vuex-module-decorators";

import * as websocket from "@/services/websocket";
import { PlayerModel } from "@/models/player/playerModel";
import { PlayerDetailsModel } from "@/models/player/playerDetailsModel";
import { config } from "@/config";
import { RdyModel } from "@/models/player/rdyModel";
import { SelectionModel } from "@/models/lobby/selectionModel";
import { PlayerCreationModel } from "@/models/player/playerCreationModel";
import axios, { AxiosResponse } from "axios";

@Module
export default class PlayerModule extends VuexModule {
<<<<<<< HEAD


    private currentPlayer: PlayerModel = {
        id: -1,
        name: "",
        lobbyOwner: false,
        role: "",
        side: "",
        rdyState: false,
        passed: false,
    };

    private playerSelected = false;
    private nameError = false;

    @Mutation
    private REMOVE_CURRENT_PLAYER(): void {
        this.currentPlayer = {
            id: -1,
            role: "",
            side: "",
            rdyState: false,
            name: "",
            lobbyOwner: false,
            passed: false,
        }
        this.playerSelected = false;
    }

    @Mutation
    private SET_NAME_ERROR(nameError: boolean) {
        this.nameError = nameError;
    }

    @Mutation
    private UPDATE_PLAYER(playerModel: PlayerModel): void {
        this.currentPlayer = playerModel;
    }

    @Mutation
    private SET_PLAYER_SELECTED(playerSelected: boolean): void {
        this.playerSelected = playerSelected;
    }

    @Action({rawError: true})
    public subscribeToPlayerChange(): void {
        const lobbyId: string = this.context.getters["lobbyId"];
        websocket.subscribe(config.PLAYER_SUBSCRIPTION_PATH + lobbyId + "/" + this.currentPlayer.id,
            messageBody => {
                if (messageBody) {
                    this.context.commit("UPDATE_PLAYER", messageBody);
                }
            });
    };

    @Action({rawError: true})
    public async checkSelectedPlayer() {
        const currentPlayerId = localStorage.getItem('currentPlayerId');
        if (currentPlayerId) {
            const existingPlayer: PlayerDetailsModel = {
                lobbyName: this.context.getters["lobbyId"],
                id: Number(currentPlayerId),
            }
            this.context.dispatch("setReturningPlayer", existingPlayer);
        }
    }

    @Action({rawError: true})
    public async setReturningPlayer(returnPlayer: PlayerDetailsModel) {
        const resp: AxiosResponse = await axios.post(process.env.VUE_APP_BASE_URL + "/updateVisiblePlayer", returnPlayer);
        if (resp.status === 200) {
            this.context.commit("SET_PLAYER_SELECTED", true);
            this.context.commit("UPDATE_PLAYER", resp.data);
            this.context.dispatch("sendLobbyUpdate");
        }
    }

    @Action({rawError: true})
    public sendReadyState(): void {
        const readyModel: RdyModel = {
            playerId: this.currentPlayer.id,
            rdyState: !this.currentPlayer.rdyState,
        }
        websocket.send(config.PLAYER_SET_READY_PATH, readyModel);
    }

    @Action({rawError: true})
    public sendSelection(selectionModel: SelectionModel): void {

        websocket.send(config.PLAYER_ROLE_SELECTION_PATH, selectionModel);
    }

    @Action({rawError: true})
    public async sendPlayerCreation(playerCreationModel: PlayerCreationModel) {
        axios.post(process.env.VUE_APP_BASE_URL + "/createPlayer", playerCreationModel).then(resp => {
            if (resp.status === 201) {
                const player: PlayerModel = resp.data;
                this.context.commit("UPDATE_PLAYER", player);
                this.context.dispatch("sendLobbyUpdate");
            }
        }).catch(() => this.context.commit("SET_NAME_ERROR", true));


    }

    get isRoleSelected(): boolean {
        return this.currentPlayer.role === "NOT_SELECTED";
    }

    get player(): PlayerModel {
        return this.currentPlayer;
    }

    get isCurrentPlayerOwner(): boolean {
        return this.currentPlayer.lobbyOwner;
    }

    get currentPlayerId(): number {
        return this.currentPlayer.id;
    }

    get currentPlayerName(): string {
        return this.currentPlayer.name;
    }

    get currentPlayerRole(): string {
        return this.currentPlayer.role;
    }

    get currentPlayerSide(): string {
        return this.currentPlayer.side;
    }

    get isPlayerSelected(): boolean {
        return this.currentPlayer.id !== -1;
    }

    get currentPlayerPassed(): boolean {
        return this.currentPlayer.passed;
    }

    get isCurrentPlayerSpymaster(): boolean {
        return this.currentPlayer.role === "SPYMASTER";
    }

    get isNameError(): boolean {
        return this.nameError;
    }


}
=======
  private currentPlayer: PlayerModel = {
    id: -1,
    name: "",
    lobbyOwner: false,
    role: "",
    side: "",
    rdyState: false
  };

  private playerSelected = false;

  @Mutation
  private REMOVE_CURRENT_PLAYER(): void {
    this.currentPlayer = {
      id: -1,
      role: "",
      side: "",
      rdyState: false,
      name: "",
      lobbyOwner: false
    };
    this.playerSelected = false;
  }

  @Mutation
  private UPDATE_PLAYER(playerModel: PlayerModel): void {
    this.currentPlayer = playerModel;
  }

  @Mutation
  private SET_PLAYER_SELECTED(playerSelected: boolean): void {
    this.playerSelected = playerSelected;
  }

  @Action({ rawError: true })
  public subscribeToPlayerChange(): void {
    const lobbyId: string = this.context.getters["lobbyId"];
    websocket.subscribe(
      config.PLAYER_SUBSCRIPTION_PATH + lobbyId + "/" + this.currentPlayer.id,
      messageBody => {
        if (messageBody) {
          this.context.commit("UPDATE_PLAYER", messageBody);
        }
      }
    );
  }

  @Action({ rawError: true })
  public async checkSelectedPlayer() {
    const currentPlayerId = localStorage.getItem("currentPlayerId");
    if (currentPlayerId) {
      const existingPlayer: PlayerDetailsModel = {
        lobbyName: this.context.getters["lobbyId"],
        id: Number(currentPlayerId)
      };
      this.context.dispatch("setReturningPlayer", existingPlayer);
    }
  }

  @Action({ rawError: true })
  public async setReturningPlayer(returnPlayer: PlayerDetailsModel) {
    const resp: AxiosResponse = await axios.post(
      process.env.VUE_APP_BASE_URL + "/updateVisiblePlayer",
      returnPlayer
    );
    if (resp.status === 200) {
      this.context.commit("SET_PLAYER_SELECTED", true);
      this.context.commit("UPDATE_PLAYER", resp.data);
      this.context.dispatch("sendLobbyUpdate");
    }
  }

  @Action({ rawError: true })
  public sendReadyState(): void {
    const readyModel: RdyModel = {
      playerId: this.currentPlayer.id,
      rdyState: !this.currentPlayer.rdyState
    };
    websocket.send(config.PLAYER_SET_READY_PATH, readyModel);
  }

  @Action({ rawError: true })
  public sendSelection(selectionModel: SelectionModel): void {
    websocket.send(config.PLAYER_ROLE_SELECTION_PATH, selectionModel);
  }

  @Action({ rawError: true })
  public async sendPlayerCreation(playerCreationModel: PlayerCreationModel) {
    const resp = await axios.post(
      process.env.VUE_APP_BASE_URL + "/createPlayer",
      playerCreationModel
    );
    if (resp.status === 201) {
      const player: PlayerModel = resp.data;
      this.context.commit("UPDATE_PLAYER", player);
    }
    await this.context.dispatch("sendLobbyUpdate");
  }

  get isRoleSelected(): boolean {
    return this.currentPlayer.role === "NOT_SELECTED";
  }

  get player(): PlayerModel {
    return this.currentPlayer;
  }

  get isCurrentPlayerOwner(): boolean {
    return this.currentPlayer.lobbyOwner;
  }

  get currentPlayerId(): number {
    return this.currentPlayer.id;
  }

  get currentPlayerName(): string {
    return this.currentPlayer.name;
  }

  get currentPlayerRole(): string {
    return this.currentPlayer.role;
  }

  get currentPlayerSide(): string {
    return this.currentPlayer.side;
  }

  get isPlayerSelected(): boolean {
    return this.currentPlayer.id !== -1;
  }
}
>>>>>>> set hungarian language option with i18n
