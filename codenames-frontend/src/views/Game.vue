<template>
    <div class="game row m-0">
        <div class="col-sm-12 col-lg-8 col-xl-9">
            <div class="row mx-0">
                <div class="col-lg-4 p-0">
                    <div class="game-header white-backgrounded-div mb-sm-2 mb-lg-4">
                        <game-counters></game-counters>
                    </div>
                </div>

                <div class="col-lg-8 pr-0 pl-lg-4">
                    <div class="game-header white-backgrounded-div mb-sm-2 mb-lg-4">
                        <puzzle></puzzle>
                    </div>
                </div>
            </div>

            <div class="game-map white-backgrounded-div col-lg-10 offset-lg-2 pr-0">
                <game-map></game-map>
            </div>

            <div class="game-options-decor">
                <img :src="spyGameUrl" alt="spy">
                <div class="game-options white-backgrounded-div mb-sm-2 mb-lg-4">
                    <game-options :is-spy-master="isSpyMaster"></game-options>
                </div>
            </div>
        </div>

        <div class="col-sm-12 col-lg-4 col-xl-3">
            <div class="player-list white-backgrounded-div col-sm-12 text-left mb-sm-2 mb-lg-4">
                <player-list :is-in-lobby="false"></player-list>
            </div>
            <chat></chat>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import GameCounters from "@/components/game/GameCounters.vue";
    import GameMap from "@/components/game/GameMap.vue";
    import Chat from "@/components/chat/Chat.vue";
    import PlayerList from "@/components/player/PlayerList.vue";
    import Puzzle from "@/components/game/Puzzle.vue";
    import GameOptions from "@/components/game/GameOptions.vue";

    @Component({
        components: {Puzzle, PlayerList, Chat, GameMap, GameCounters, GameOptions}
    })
    export default class Game extends Vue {
        private spyGameUrl = require("../assets/spy_game.png");
        private isSpyMaster = false;

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

    .game-options {
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

    .game-options.white-backgrounded-div {
        background-color: black;
    }

    .game-options-decor {
        position: absolute;
        bottom: 0;
        left: -7vw;
    }


    .game-options-decor img {
        width: 25vw;
    }

</style>