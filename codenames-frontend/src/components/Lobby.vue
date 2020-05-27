<template>
    <div class="main-div row m-0">
        <div id="women-spy" :class="['spy-decor', {'left-out':isMouseInMiddle}]">
        </div>
        <div class="col-sm-12 col-xl-8 offset-xl-2"
             @mouseenter="isMouseInMiddle = true"
             @mouseleave="isMouseInMiddle = false">
            <template v-if="!isPlayerSelected">
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
                    <chat></chat>
                    <div class="mb-3">
                        <RolePick></RolePick>
                    </div>
                </div>

                <div class="col-sm-12 col-lg-4">
                    <div class="player-list white-backgrounded-div text-left row mx-0 mb-3">
                        <player-list :is-in-lobby="true"></player-list>
                    </div>

                    <div class="lobby-options-div col-sm-12">
                        <LobbyOption v-if="currentPlayer.lobbyOwner"></LobbyOption>
                    </div>

                </div>

                <div class="col-sm-12">
                    <b-input-group size="sm"
                                   class="col-sm-12
                           col-md-8 col-lg-6
                           offset-md-2 offset-lg-3
                           pb-sm-2
                           pb-lg-5">
                        <b-form-input id="current-url"
                                      type="text"
                                      readonly
                                      :value="path">
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
    import Chat from "@/components/chat/Chat.vue";
    import LobbyOption from "@/components/lobby/LobbyOption.vue";
    import {PlayerCreationModel} from "@/models/playerCreationModel";
    import {PlayerModel} from "@/models/playerModel";
    import KickPlayer from "@/components/player/KickPlayer.vue";
    import ReadyCheck from "@/components/player/ReadyCheck.vue";
    import RolePick from "@/components/player/RolePick.vue";
    import * as websocket from '@/services/websocket'
    import router from "@/router";
    import {config} from "@/config";
    import {PlayerDetailsModel} from "@/models/playerDetailsModel";
    import PlayerList from "@/components/player/PlayerList.vue";

    @Component({
        components: {Chat, PlayerList, RolePick, ReadyCheck, KickPlayer, LobbyOption}
    })
    export default class Lobby extends Vue {
        private logoUrl = require("../assets/semanedoc.png");
        private isMouseInMiddle = true;

        private path = "http://localhost:4200";
        private currentPlayerName = "";


        @Watch("currentPlayer.id")
        private subscribeToPlayerChange() {
            if (this.currentPlayer.id !== -1) {
                localStorage.setItem('currentPlayerId', JSON.stringify(this.currentPlayer.id));
                this.$store.dispatch("subscribeToPlayerChange");
            }
        }

        constructor() {
            super();
            window.addEventListener('beforeunload', this.hideLeftPlayer);
        }

        private hideLeftPlayer(): void {
            const playerDetails: PlayerDetailsModel = {
                id: this.currentPlayer.id,
                lobbyName: this.lobbyId,
            }
            websocket.send(config.HIDE_PLAYER, playerDetails);
        }

        async mounted() {
            await websocket.connect();
            this.path += this.$route.path;
            const joined: boolean = await this.$store.dispatch('joinLobby', {lobbyId: this.$route.params.lobbyId});
            if (!joined) {
                router.push('/')
            } else {
                this.$store.dispatch("chatModule/subscribeToChat", this.$route.params.lobbyId);
            }
        };

        public copyPath(): void {
            this.$copyText(this.path);
        }

        public createPlayer(): void {
            this.$store.commit("SET_PLAYER_SELECTED", true);
            const newPlayer: PlayerCreationModel = {
                lobbyName: this.$route.params.lobbyId,
                name: this.currentPlayerName,
            }
            websocket.send(process.env.VUE_APP_OPTIONS_CREATE, newPlayer);
        }

        get currentPlayer(): PlayerModel {
            return this.$store.getters["getCurrentPlayer"];
        }

        get isPlayerSelected(): boolean {
            return this.$store.getters["getPlayerSelected"]
        }

        get lobbyId(): string {
            return this.$store.getters["lobbyId"];
        }
    }
</script>

<style scoped>

    .background-div {
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.6;
    }

    .player-list {
        height: 60vh;
    }

    .white-backgrounded-div {
        width: 100%;
        background-color: rgba(255, 255, 255, 0.6);
        overflow-y: scroll;
        overflow-x: hidden;
        -ms-overflow-style: none;
    }

    .white-backgrounded-div::-webkit-scrollbar {
        display: none;
    }

    .white-backgrounded-div {
        -ms-overflow-style: none;
    }

    .lobby-options-div {
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

    button {
        background-color: rgb(135, 25, 75);
        border: 0 solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
        outline: rgba(135, 25, 75, .5) solid 1px;
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

    input:read-only {
        opacity: 0.6;
    }

    input {
        opacity: 0.6;
    }

    input:focus {
        opacity: 1;
        outline: none;
        box-shadow: none;
        border: none;
    }
</style>