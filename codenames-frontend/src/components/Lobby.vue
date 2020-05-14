<template>
    <div class="main-div col-sm-12">
        <template v-if="!playerSelected">
            <div class="codenames-header">
                <b-img :src="logoUrl"></b-img>
            </div>
            <div class="col-sm-12 col-md-8 col-lg-6 col-xl-4 offset-md-2 offset-lg-3 offset-xl-4">
                <b-input-group size="md">
                    <b-form-input id="current-player"
                                  type="text"
                                  placeholder="Enter your name"
                                  v-on:keyup.enter="createPlayer"
                                  v-model="currentPlayerName">
                    </b-form-input>
                    <b-input-group-append>
                        <b-button squared
                                  type="submit"
                                  @click="createPlayer">Ok</b-button>
                    </b-input-group-append>
                </b-input-group>
            </div>
        </template>
        <div v-else>
            <input class="input" type="text" readonly
                   :value="path"/>
            <button type="button"
                    @click="copyPath">Szoba cím másolása
            </button>
            <div v-for="player in players"
                 :key="player.id">
                <button v-if="player.name!==currentPlayerName"
                        type="button"
                        @click="initKickPlayer(player)">Kidob
                </button>
                {{player.name}}-{{player.role}}-{{player.side}}
            </div>


            <KickPlayer
                    :stomp-client="stompClient"
                    :kick-init-player="currentPlayer"
                    :player-to-kick="playerToKick"
            ></KickPlayer>


            <lobby-chat :current-player="currentPlayerName"
                        :current-lobby="this.$route.params.lobbyId"
                        :stomp-client="stompClient"></lobby-chat>
            <LobbyOption v-if="currentPlayer.lobbyOwner"
                         :lobby-name="this.$route.params.lobbyId"
                         :stomp="stompClient"
            ></LobbyOption>
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

    @Component({
        components: {KickPlayer, LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {
        private logoUrl = require("../assets/codenames.png");
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
            return this.$store.getters["getPlayers"];
        }

        get currentPlayer(): PlayerModel {
            return this.$store.getters["getCurrentPlayer"];
        }
    }
</script>

<style scoped>
    .input {
        width: 500px
    }

    label {
        color: white;
    }

    .main-div {
        background-image: url("../assets/background.svg");
        background-repeat: no-repeat;
        background-size: 100%;
        width: 100vw;
        height: 100vh;
    }


    img {
        max-width: 75%;
        margin-bottom: 15vh;
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