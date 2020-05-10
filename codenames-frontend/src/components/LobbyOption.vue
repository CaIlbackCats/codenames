<template>
    <div>
        <!--        <input type="text" v-model="playerName">-->
        <!--        <button @click="addPlayer">Ember Hozzáadás</button>-->
        <button @click="randomizeRoles">Véletlenszerű szerep kiosztás</button>
        <button @click="randomizeSide">Véletlenszerű csapat kiosztás</button>
        <div v-for="player in players" :key="player.id">{{player.name}}-{{player.role}}-{{player.side}}</div>
    </div>

</template>

<script lang="ts">

    import {Component, Prop, Vue} from "vue-property-decorator";
    import {PlayerModel} from "@/models/playerModel";
    import {Client} from "webstomp-client";


    @Component
    export default class LobbyOption extends Vue {

        @Prop()
        private readonly lobbyName!: string;

        @Prop()
        private stomp!: Client;

        constructor() {
            super();
            this.$store.dispatch("fetchPlayers", this.lobbyName);
        }

        public randomizeRoles(): void {
            this.$store.dispatch("setPlayerRole", this.lobbyName);
        }

        public randomizeSide(): void {
            this.$store.dispatch("setPlayerSide", this.lobbyName);
        }

        get players(): Array<PlayerModel> {
            return this.$store.getters.getPlayers;
        }

    }
</script>

<style scoped>

</style>