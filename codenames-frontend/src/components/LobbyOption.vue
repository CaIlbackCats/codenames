<template>
    <div class="align-center">
        <b-button :title="partySize<MIN_PARTY_SIZE ? 'There must be at least 4 players to use this function': '' "
                  size="sm" block squared @click="randomizeRoles" :disabled="partySize<MIN_PARTY_SIZE">Random role</b-button>
        <b-button :title="partySize<MIN_PARTY_SIZE ? 'There must be at least 4 players to use this function': '' "
                  size="sm" block squared @click="randomizeSide" :disabled="partySize<MIN_PARTY_SIZE">Random side</b-button>
        <b-button :title="getEveryOneRdy ? '' : '' "
                size="md" block squared :disabled="!getEveryOneRdy" @click="createGame" class="mt-3">Start The Game!</b-button>
    </div>
</template>

<script lang="ts">

    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import * as websocket from '@/services/websocket'
    import router from "@/router";
    import {config} from "@/config";

    @Component
    export default class LobbyOption extends Vue {

        private MIN_PARTY_SIZE = 4;

        @Watch("gameId")
        private navigateToGame() {
            if(this.gameId != -1) {
                router.push({name: "Game", params: {lobbyId: this.lobbyId, gameId: "" + this.gameId}});
            }
        }

        constructor() {
            super();
        }

        public createGame(): void {
            this.$store.dispatch("createGame");
        }

        public randomizeRoles(): void {
            this.$store.dispatch("sendRandomizeRole");
        }

        public randomizeSide(): void {
            this.$store.dispatch("sendRandomizeSide");
        }

        get getEveryOneRdy(): boolean {
            return this.$store.getters["isEveryOneRdy"];
        }

        get lobbyId(): string {
            return this.$store.getters["lobbyId"];
        }

        get partySize(): number {
            return this.$store.getters["partySize"];
        }

        get gameId(): number {
            return this.$store.getters["gameId"];
        }
    }
</script>

<style scoped>
    small{
        color: rgb(135, 25, 75);
        opacity: 0.6;
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

    button:enabled:hover {
        background-color: rgb(135, 25, 75);
        border: 0px solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, .5), 0 0 20px rgba(250, 230, 15, .2);
        outline-color: rgba(250, 230, 15, 0);
        outline-offset: 15px;
        text-shadow: 1px 1px 2px #427388;
    }
</style>