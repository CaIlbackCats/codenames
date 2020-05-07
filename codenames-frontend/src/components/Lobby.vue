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
            <lobby-chat :current-player="currentPlayer" :current-lobby="this.$route.params.lobbyId"></lobby-chat>
            <LobbyOption :lobby-name="this.$route.params.lobbyId"></LobbyOption>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import LobbyChat from "@/components/LobbyChat.vue";
    import LobbyOption from "@/components/LobbyOption.vue";
    import {PlayerCreationModel} from "@/models/playerCreationModel";

    @Component({
        components: {LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {

        private path = "http://localhost:4200";
        private currentPlayer = "";
        private playerSelected = false;

        constructor() {
            super();
        };

        mounted() {
            // if (this.$store.lobby !== undefined) {
            //     dispatch('joinLobby')
            // }
            this.path += this.$route.path;
        };

        public copyPath(): void {
            this.$copyText(this.path);
        }

        public createPlayer() {
            this.playerSelected = !this.playerSelected;

            const newPlayer: PlayerCreationModel = {
                lobbyName: this.$route.params.lobbyId,
                name: this.currentPlayer,
            }
            this.$store.dispatch("addNewPlayer", newPlayer);
        }

    }
</script>

<style scoped>
    .input {
        width: 500px
    }
</style>