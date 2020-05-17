<template>
    <div class="main-div row m-0">
        <div id="women-spy" :class="['spy-decor', {'left-out':isMouseInMiddle}]">
        </div>
        <div class="col-sm-12 col-xl-8 offset-xl-2"
             @mouseenter="isMouseInMiddle = true"
             @mouseleave="isMouseInMiddle = false">
            <template v-if="!playerSelected">
                <div class="codenames-header">
                    <b-img :src="logoUrl"></b-img>
                </div>
                <div class="col-sm-12 col-md-8 col-lg-6 col-xl-4 offset-md-2 offset-lg-3 offset-xl-4">
                    <b-input-group size="md">
                        <b-form-input id="current-player"
                                      type="text"
                                      maxlength="10"
                                      placeholder="Enter your name"
                                      v-on:keyup.enter="createPlayer"
                                      v-model="currentPlayerName">
                        </b-form-input>
                        <b-input-group-append>
                            <b-button squared
                                      type="submit"
                                      @click="createPlayer">Ok
                            </b-button>
                        </b-input-group-append>
                    </b-input-group>
                </div>
            </template>

            <div v-else class="row pt-5 m-0 p-4">
                <div class="col-sm-12 col-lg-8">
                    <lobby-chat :current-player="currentPlayer"
                                :current-lobby="this.$route.params.lobbyId"
                                :stomp-client="stompClient"></lobby-chat>
                </div>

                <div class="col-sm-12 col-lg-4">
                    <div class="players-div text-left row mx-0 mb-3">
                        <div class="list-div">
                            <div class="m-2" v-for="player in players"
                                 :key="player.id">
                                <ReadyCheck
                                        :stomp-client="stompClient"
                                        :player="player"></ReadyCheck>
                                <font-awesome-icon class="mr-2" v-if="player.id === currentPlayer.id" icon="user-secret"/>
                                <label class="mr-2" :style="{ color: player.side}">
                                    {{player.name}}</label> <!---{{player.role}}-{{player.side}} -->
                                <font-awesome-icon v-if="player.name!==currentPlayerName"
                                                   class="kick-btn"
                                                   @click="initKickPlayer(player)"
                                                   icon="user-minus"/>
                            </div>
                        </div>
                        <KickPlayer
                                :stomp-client="stompClient"
                                :kick-init-player="currentPlayer"
                                :player-to-kick="playerToKick"
                        ></KickPlayer>
                        <div class="white-background-div"></div>
                    </div>

                    <div class="lobby-options-div col">
                        <div style="position: absolute; right: 0" class="list-div">
                            <LobbyOption v-if="currentPlayer.lobbyOwner"
                                         :lobby-name="this.$route.params.lobbyId"
                                         :stomp="stompClient"
                            ></LobbyOption>
                        </div>
                    </div>

                </div>

                <div class="col-sm-12">
                    <b-input-group size="sm"
                                   class="col-sm-12
                           col-md-8 col-lg-6
                           offset-md-2 offset-lg-3
                           pb-sm-2
                           pb-lg-5">
                        <b-form-input id="current-player"
                                      type="text"
                                      readonly
                                      :value="path"
                                      v-on:keyup.enter="createPlayer">
                        </b-form-input>
                        <b-input-group-append>
                            <b-button squared
                                      type="button"
                                      @click="copyPath">Copy link
                            </b-button>
                        </b-input-group-append>
                    </b-input-group>
                </div>
            </div>
        </div>
        <div id="man-spy" :class="['spy-decor',{'right-out':isMouseInMiddle}]">
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import LobbyChat from "@/components/LobbyChat.vue";
    import LobbyOption from "@/components/LobbyOption.vue";
    import {PlayerCreationModel} from "@/models/playerCreationModel";
    import SockJS from "sockjs-client";
    import Stomp, {Client} from "webstomp-client";
    import {PlayerModel} from "@/models/playerModel";
    import {RoomModel} from "@/models/roomModel";
    import KickPlayer from "@/components/KickPlayer.vue";
    import {PlayerRemovalModel} from "@/models/playerRemovalModel";
    import ReadyCheck from "@/components/ReadyCheck.vue";

    @Component({
        components: {ReadyCheck, KickPlayer, LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {
        private logoUrl = require("../assets/semanedoc.png");
        private isMouseInMiddle = true;

        private stompClient!: Client;
        private path = "http://localhost:4200";
        private currentPlayerName = "";
        private playerSelected = false;
        private playerToKick: PlayerModel = {
            id: -1,
            name: "",
            lobbyOwner: false,
            role: "",
            side: "",
            rdyState: false,
        };

        @Watch("currentPlayer")
        private subscribeToPlayerChange() {
            const room: RoomModel = {
                name: this.$route.params.lobbyId,
                stompClient: this.stompClient,
                playerId: this.currentPlayer.id,
            }
            this.$store.dispatch("subscribeToPlayerChange", room);
        }

        //  private showKickWindow = false;

        constructor() {
            super();
        };

        mounted() {
            this.path += this.$route.path;
            this.$store.dispatch('joinLobby', this.$route.params.lobbyId)
            this.connect();
        };

        public connect(): void {
            const socket = new SockJS(process.env.VUE_APP_BASE_URL);
            this.stompClient = Stomp.over(socket);

            //kikapcsolja a loggolást
            // this.stompClient.debug = () => {
            //     null
            // };
            this.stompClient.connect({"name": this.currentPlayerName}, frame => {
                this.subscribeToLobby();
            });
        }

        private subscribeToLobby() {
            const room: RoomModel = {
                name: this.$route.params.lobbyId,
                stompClient: this.stompClient,
            }
            this.$store.dispatch("subscribeToLobby", room);
        }


        public copyPath(): void {
            this.$copyText(this.path);
        }

        public createPlayer(): void {
            this.playerSelected = !this.playerSelected;

            const newPlayer: PlayerCreationModel = {
                lobbyName: this.$route.params.lobbyId,
                name: this.currentPlayerName,
            }
            this.stompClient.send(process.env.VUE_APP_OPTIONS_CREATE, JSON.stringify(newPlayer));
            //  this.subscribeToPlayerChange();

        }

        public initKickPlayer(player: PlayerModel): void {
            this.playerToKick = player;
            // this.showKickWindow = true;
            if (this.currentPlayer.lobbyOwner) {
                const playerRemovalModel: PlayerRemovalModel = {
                    kickType: "OWNER",
                    ownerId: this.currentPlayer.id,
                    playerToRemoveId: player.id,
                }
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_INIT, JSON.stringify(playerRemovalModel));
            } else {

                const votingPlayers = this.players.filter(player => player.name !== this.playerToKick.name);
                const playerRemovalModel: PlayerRemovalModel = {
                    kickType: "VOTE",
                    ownerId: this.currentPlayer.id,
                    votingPlayers: votingPlayers,
                    playerToRemoveId: player.id,
                }
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_INIT, JSON.stringify(playerRemovalModel));

                //   this.players.filter(player => player.name !== this.playerToKick.name).forEach(player => {
                //       this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_INIT, player.name);
                //   });
            }
        }

        get players(): Array<PlayerModel> {
            // TODO: refactor
            const playersList: Array<PlayerModel> = [];
            const playersFetched: Array<PlayerModel> = this.$store.getters["getPlayers"];
            const currentPlayer = playersFetched.find(player => player.id === this.currentPlayer.id);
            if(currentPlayer){
                playersList.push(currentPlayer);
            }
            playersFetched.filter(player => player.id != this.currentPlayer.id)
                .forEach(player => playersList.push(player));
            return playersList;
        }

        get currentPlayer(): PlayerModel {
            return this.$store.getters["getCurrentPlayer"];
        }
    }
</script>

<style scoped>

    svg {
        color: rgb(135, 25, 75);
    }

    .white-background-div {
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.3;
    }

    .players-div {
        height: 60vh;
    }

    .list-div {
        max-height: 60vh;
        overflow-y: scroll;
        position: absolute;
        z-index: 2;
        -ms-overflow-style: none;
    }

    .list-div::-webkit-scrollbar {
        display: none;
    }

    .lobby-options-div{
        height: 15vh;
    }

    label {
        color: white;
    }

    .main-div {
        background-image: url("../assets/background.svg");
        background-repeat: no-repeat;
        background-size: cover;
        background-position: top;
        min-height: 100vh;
        min-width: 100vw;
    }

    @media (max-width: 1025px) {
        #women-spy {
            display: none;
        }

        #man-spy {
            display: none;
        }
    }

    .spy-decor {
        overflow: hidden;
        background-repeat: no-repeat;
        background-size: cover;
        position: absolute;
        height: 100vh;
        width: 25vw;
        z-index: 1;
        transition: 1.5s;
    }

    #women-spy {
        background-image: url("../assets/spy_woman.png");
        background-position: top 0 right 0;
        left: 0;
    }

    #man-spy {
        background-image: url("../assets/spy_man.png");
        background-position: top 0 left 0;
        right: 0;
    }

    .spy-decor.left-out {
        transform: translateX(-20%);
        -webkit-transform: translateX(-20%);
    }

    .spy-decor.right-out {
        transform: translateX(20%);
        -webkit-transform: translateX(20%);
    }

    img {
        max-width: 75%;
        margin-bottom: 15vh;
    }

    .kick-btn {
        opacity: 0.2;
    }

    .kick-btn:hover {
        opacity: 0.8;
    }

    button {
        background-color: rgb(135, 25, 75);
        border: 0 solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
        outline: 1px solid;
        outline-color: rgba(135, 25, 75, .5);
        outline-offset: 0px;
        text-shadow: none;
        transition: all 1250ms cubic-bezier(0.19, 1, 0.22, 1);
    }

    button:hover {
        background-color: rgb(135, 25, 75);
        border: 0px solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, .5), 0 0 20px rgba(250, 230, 15, .2);
        outline-color: rgba(250, 230, 15, 0);
        outline-offset: 15px;
        text-shadow: 1px 1px 2px #427388;
    }
</style>