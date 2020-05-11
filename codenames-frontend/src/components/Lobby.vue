<template>
    <div>
        <div v-if="!playerSelected">
            <label for="current-player">Add meg a játékos neved</label>
            <input id="current-player" type="text" v-model="currentPlayerName">
            <button @click="createPlayer">Kész!</button>
        </div>
        <div v-if="playerSelected">
            <input class="input" type="text" readonly
                   :value="path"/>
            <button type="button"
                    @click="copyPath">Szoba cím másolása
            </button>
            <div v-for="player in players"
                 :key="player.id">
                <button v-if="player.name!==currentPlayerName"
                        @click="initKickPlayer(player)">Kidob
                </button>
                {{player.name}}-{{player.role}}-{{player.side}}
            </div>


            <KickPlayer v-if="showKickWindow"
                        :stomp-client="stompClient"
                        :kick-init-player="currentPlayer"
                        :player-to-kick="playerToKick"
                        :show-window="showKickWindow"
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
    import {Component, Vue} from "vue-property-decorator";
    import LobbyChat from "@/components/LobbyChat.vue";
    import LobbyOption from "@/components/LobbyOption.vue";
    import {PlayerCreationModel} from "@/models/playerCreationModel";
    import SockJS from "sockjs-client";
    import Stomp, {Client} from "webstomp-client";
    import {PlayerModel} from "@/models/playerModel";
    import {RoomModel} from "@/models/roomModel";
    import KickPlayer from "@/components/KickPlayer.vue";

    @Component({
        components: {KickPlayer, LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {

        private stompClient!: Client;
        private path = "http://localhost:4200";
        private currentPlayerName = "";
        private playerSelected = false;
        private playerToKick!: PlayerModel;
        private showKickWindow = false;

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
                this.subscribeToOptions();
            });
        }

        private subscribeToOptions() {
            const room: RoomModel = {
                name: this.$route.params.lobbyId,
                stompClient: this.stompClient,
            }
            this.$store.dispatch("subscribeToOptions", room);
        }

        private subscribeToPlayerChange() {
            const room: RoomModel = {
                name: this.$route.params.lobbyId,
                stompClient: this.stompClient,
                playerName: this.currentPlayerName,
            }
            this.$store.dispatch("subscribeToPlayerChange", room);
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
            this.subscribeToPlayerChange();

        }

        public initKickPlayer(player: PlayerModel): void {
            this.playerToKick = player;
            this.showKickWindow = true;
            if (this.currentPlayer.lobbyOwner) {
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_INIT, this.currentPlayer.name);
            } else {
                this.players.filter(player => player.name !== this.playerToKick.name).forEach(player => {
                    this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_INIT, player.name);
                });
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
</style>