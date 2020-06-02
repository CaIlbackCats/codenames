<template>
    <div class="game">
        <div class="row m-0" v-if="!isEndGame">
            <div class="col-sm-12 col-lg-8 col-xl-9">
                <div class="row mx-0">
                    <div class="col-lg-4 p-0">
                        <div class="game-header white-backgrounded-div mb-sm-2 mb-lg-4">
                            <game-counters></game-counters>
                        </div>
                    </div>

                    <div class="col-lg-8 pr-0 pl-lg-4">
                        <div class="game-header white-backgrounded-div mb-sm-2 mb-lg-4" id="puzzle-words">
                            <puzzle></puzzle>
                        </div>
                    </div>
                </div>

                <div class="game-map white-backgrounded-div col-lg-10 offset-lg-2 px-0">
                    <game-map></game-map>
                </div>

            <div class="game-options-decor">
                <game-options></game-options>
            </div>
        </div>

            <div class="col-sm-12 col-lg-4 col-xl-3">
                <div class="player-list white-backgrounded-div col-sm-12 text-left mb-sm-2 mb-lg-4">
                    <player-list :is-in-lobby="false"></player-list>
                </div>
                <chat></chat>
            </div>
        </div>
        <div class="row m-0" v-else>
            <game-end></game-end>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import GameCounters from "@/components/game/GameCounters.vue";
    import GameMap from "@/components/game/GameMap.vue";
    import Chat from "@/components/chat/Chat.vue";
    import PlayerList from "@/components/player/PlayerList.vue";
    import Puzzle from "@/components/game/Puzzle.vue";
    import GameOptions from "@/components/game/GameOptions.vue";
    import GameEnd from "@/components/game/GameEnd.vue";
    import * as websocket from '@/services/websocket'

    @Component({
        components: {GameEnd, Puzzle, PlayerList, Chat, GameMap, GameCounters, GameOptions}
    })
    export default class Game extends Vue {

        get puzzleWordsSize(): number {
            return this.$store.getters["puzzleWordsSize"]
        }

        @Watch("puzzleWordsSize")
        private scrollDown() {
            const container: HTMLElement | null = document.getElementById("puzzle-words");
            container!.scrollTop = container!.scrollHeight;
        }

        async mounted() {
            await websocket.connect();
            await this.$store.dispatch("subscribeToGame");
            await this.$store.dispatch("subscribeToGameRoles");
            await this.$store.dispatch("fetchActiveGame");
        }

        get gameId(): number {
            return this.$store.getters["gameId"];
        }

        get isEndGame() {
            return this.$store.getters["isEndGame"];
        }

        get currentGameId() {
            return this.$store.getters["currentGameId"];
        }
    }
</script>

<style>
    .game {
        background-image: url("../assets/background.svg");
        background-repeat: no-repeat;
        background-size: cover;
        background-position: top;
        min-height: 100vh;
        min-width: 100vw;
    }

    .game-header {
        height: 7vh;
    }

    .game-map {
        height: 80vh;
    }

    .player-list {
        height: 21vh;
    }

    .game-options-decor {
        height: 14vh;
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
</style>