<template>
    <div class="main-div row m-0">
        <div id="women-spy" :class="['spy-decor', {'left-out':isMouseInMiddle}]">
        </div>
        <div class="col-sm-12 col-xl-8 offset-xl-2"
             @mouseenter="isMouseInMiddle = true"
             @mouseleave="isMouseInMiddle = false">
            <CreatePlayer v-if="!isPlayerSelected">
            </CreatePlayer>

            <div v-else class="row pt-5 m-0 p-4">
                <div class="col-sm-12 col-lg-8">
                    <lobby-chat
                    ></lobby-chat>
                    <div class="col-sm-12 mb-3">
                        <RolePick></RolePick>
                    </div>
                </div>

                <div class="col-sm-12 col-lg-4">
                    <div class="players-div text-left row mx-0 mb-3">
                        <div class="list-div">
                            <div class="m-2" v-for="player in players"
                                 :key="player.id">
                                <ReadyCheck :player="player"></ReadyCheck>
                                <font-awesome-icon class="mr-2" v-if="player.id === currentPlayerId"
                                                   icon="user-secret"/>
                                <font-awesome-icon v-if="player.role === 'SPYMASTER'"
                                                   :icon="['fas', 'briefcase']"
                                                   :class="[ 'mr-2', player.side === 'BLUE' ? 'blue-spymaster': 'red-spymaster'] "
                                ></font-awesome-icon>
                                <label class="mr-2" :style="player.side === 'BLUE' ? 'color: dodgerblue':
                                                        player.side === 'RED' ? 'color: indianred' : 'color: #87194B' ">
                                    {{player.name}}</label> <!---{{player.role}}-{{player.side}} -->
                                <font-awesome-icon v-if="player.name !== currentPlayerName"
                                                   class="kick-btn"
                                                   @click="initKickPlayer(player.id)"
                                                   icon="user-minus"/>
                            </div>
                        </div>
                        <KickPlayer
                        ></KickPlayer>
                        <div class="white-background-div"></div>
                    </div>

                    <div class="lobby-options-div col-sm-12">
                        <LobbyOption v-if="isCurrentPlayerLobbyOwner"
                        ></LobbyOption>
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
    import LobbyChat from "@/components/LobbyChat.vue";
    import LobbyOption from "@/components/LobbyOption.vue";
    import {PlayerCreationModel} from "@/models/playerCreationModel";
    import {PlayerModel} from "@/models/playerModel";
    import {RoomModel} from "@/models/roomModel";
    import KickPlayer from "@/components/KickPlayer.vue";
    import {PlayerRemovalModel} from "@/models/playerRemovalModel";
    import ReadyCheck from "@/components/ReadyCheck.vue";
    import RolePick from "@/components/RolePick.vue";
    import * as websocket from '@/services/websocket'
    import router from "@/router";
    import {config} from "@/config";
    import {PlayerDetailsModel} from "@/models/playerDetailsModel";
    import CreatePlayer from "@/components/CreatePlayer.vue";

    @Component({
        components: {CreatePlayer, RolePick, ReadyCheck, KickPlayer, LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {
        private isMouseInMiddle = true;

        private path = "http://localhost:4200";

        @Watch("currentPlayerId")
        private subscribeToPlayerChange() {
            if (this.currentPlayerId !== -1) {
                // localStorage.setItem('currentPlayerId', JSON.stringify(this.currentPlayer.id));
                this.$store.dispatch("subscribeToPlayerChange");
            } else if (this.currentPlayerId === -1) {
                router.push("/");
            }
        }

        constructor() {
            super();
            window.addEventListener('beforeunload', this.hideLeftPlayer);
        }

        private hideLeftPlayer(): void {
            const playerDetails: PlayerDetailsModel = {
                id: this.currentPlayerId,
                lobbyName: this.lobbyId,
            }
            websocket.send(config.HIDE_PLAYER_PATH, playerDetails);
        }

        async mounted() {
            await websocket.connect();
            this.path += this.$route.path;
            const joined: boolean = await this.$store.dispatch('joinLobby', {lobbyId: this.$route.params.lobbyId});
            if (!joined) {
                router.push('/')
            } else {
                this.$store.dispatch("chatModule/subscribeToChat", this.$route.params.lobbyId);
                await this.$store.dispatch("subscribeToLobbyRoleData");
                await this.$store.dispatch("subscribeToKick");
            }
        };

        public copyPath(): void {
            this.$copyText(this.path);
        }

        public initKickPlayer(playerToRemoveId: number): void {
            this.$store.dispatch("sendKickWindowInit", playerToRemoveId);
        }

        get players(): Array<PlayerModel> {
            return this.$store.getters["playersOrdered"];
        }

        get currentPlayerId(): number {
            return this.$store.getters["currentPlayerId"];
        }

        get isPlayerSelected(): boolean {
            return this.$store.getters["isPlayerSelected"]
        }

        get isCurrentPlayerLobbyOwner(): boolean {
            return this.$store.getters["isCurrentPlayerOwner"];
        }

        get currentPlayerName(): string {
            return this.$store.getters["currentPlayerName"]
        }

        get lobbyId(): string {
            return this.$store.getters["lobbyId"];
        }
    }
</script>

<style scoped>

    svg {
        color: rgb(135, 25, 75);
    }

    .blue-spymaster {
        color: dodgerblue;
    }

    .red-spymaster {
        color: indianred;
    }

    .white-background-div {
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.6;
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

    input {
        opacity: 0.6;
    }

    input:read-only {
        opacity: 0.6;
    }

    input:focus {
        opacity: 1;
        outline: none;
        box-shadow: none;
        border: none;
    }
</style>