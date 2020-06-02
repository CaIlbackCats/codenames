<template>
    <div class="align-center row mx-0">
        <div class="col-lg-6">
            <b-button :disabled="!isEnoughPlayersToPlay"
                      :title="isEnoughPlayersToPlay ? 'There must be at least 4 players to use this function': '' "
                      @click="randomizeRoles" block size="sm" squared>
                {{$t("lobby-option.role")}}
            </b-button>
        </div>
        <div class="col-lg-6">
            <b-button :disabled="!isEnoughPlayersToPlay"
                      :title="isEnoughPlayersToPlay ? 'There must be at least 4 players to use this function': '' "
                      @click="randomizeSide" block size="sm" squared>
                {{$t("lobby-option.side")}}
            </b-button>
        </div>

        <div class="col-sm-12">
            <img @click="setGameLanguage('ENGLISH')"
                 alt="english"
                 src="https://upload.wikimedia.org/wikipedia/commons/a/ae/Flag_of_the_United_Kingdom.svg">
            <img @click="setGameLanguage('HUNGARIAN')"
                 alt="hungarian"
                 src="https://upload.wikimedia.org/wikipedia/commons/c/c1/Flag_of_Hungary.svg">
        </div>

        <div class="col-sm-12">
            <b-button :disabled="!isGameReadyToStart"
                      :title="isGameReadyToStart ? '' :
                      !isEnoughPlayersToPlay ? 'Not enough players to play' : 'No language selected' "
                      @click="createGame" block size="md" squared>
                {{$t("lobby-option.start")}}
            </b-button>
        </div>

    </div>
</template>

<script lang="ts">

    import {Component, Vue} from "vue-property-decorator";
    import {LanguageModel} from "@/models/languageModel";

    @Component
    export default class LobbyOption extends Vue {

        public setGameLanguage(language: string): void {
            const gameLanguage: LanguageModel = {
                language: language
            }
            this.$store.dispatch("setGameLanguage", gameLanguage);
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

        get lobbyId(): string {
            return this.$store.getters["lobbyId"];
        }

        get partySize(): number {
            return this.$store.getters["partySize"];
        }

        get isGameReadyToStart(): boolean {
            return this.$store.getters["isGameReadyToStart"];
        }

        get isEnoughPlayersToPlay(): boolean {
            return this.$store.getters["isEnoughPlayersToPlay"];
        }
    }
</script>

<style scoped>
    img {
        border-radius: 50%;
        width: 3rem;
        height: 3rem;
        cursor: pointer;
        opacity: 0.7;
        margin: 0 1rem 0 1rem;
        box-sizing: content-box;
        -webkit-filter: drop-shadow(1px 1px 1px rgba(0, 0, 0, 0.5));
        filter: drop-shadow(1px 1px 1px rgba(0, 0, 0, 0.5));
    }

    img:hover {
        opacity: 1;
        cursor: pointer;
        -webkit-filter: drop-shadow(1px 1px 1px rgba(255, 255, 255, 0.7));
        filter: drop-shadow(1px 1px 1px rgba(255, 255, 255, 0.7));
    }

    small {
        color: rgb(135, 25, 75);
        opacity: 0.6;
    }

    button {
        background-color: rgb(135, 25, 75);
        border: 0 solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
        outline: rgba(135, 25, 75, .5) solid 1px;
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