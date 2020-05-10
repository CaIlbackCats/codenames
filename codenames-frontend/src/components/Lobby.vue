<template>
    <div>
        <div v-if="!playerSelected">
            <label for="current-player">Add meg a játékos neved</label>
            <input id="current-player" type="text" v-model="currentPlayer">
            <button @click="createPlayer">Kész!</button>
        </div>
        <div v-if="playerSelected">
            <input class="input" type="text" readonly :value="path"/>
            <button type="button" @click="copyPath">Szoba cím másolása</button>
            <lobby-chat :current-player="currentPlayer"
                        :current-lobby="this.$route.params.lobbyId"
                        :stomp-client="stompClient"></lobby-chat>
            <LobbyOption :lobby-name="this.$route.params.lobbyId"
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

    @Component({
        components: {LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {

        private path = "http://localhost:4200";
        private currentPlayer = "";
        private playerSelected = false;

        private stompClient!: Client;

        constructor() {
            super();
        };

        mounted() {
            // if (this.$store.lobby !== undefined) {
            //     dispatch('joinLobby')
            // }
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
            this.stompClient.connect({"name": this.currentPlayer}, frame => {
                // this.subscribe();
            })

        }

        public copyPath(): void {
            this.$copyText(this.path);
        }

        public createPlayer() {
            this.playerSelected = !this.playerSelected;

            const newPlayer: PlayerCreationModel = {
                lobbyName: this.$route.params.lobbyId,
                name: this.currentPlayer,
            }
            this.stompClient.send(process.env.VUE_APP_OPTIONS_CREATE, JSON.stringify(newPlayer));
        }

    }
</script>

<style scoped>
    .input {
        width: 500px
    }
</style>