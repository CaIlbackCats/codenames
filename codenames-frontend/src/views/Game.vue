<template>
    <div class="game row m-0">
        <div class="col-sm-12 col-lg-8 col-xl-9 pl-lg-4">
            <div class="row mx-0">
                <div class="col-lg-4 p-0">
                    <div class="game-header white-backgrounded-div mb-sm-2 mb-lg-4">
                        <div class="game-header content-div">
                            <game-counters></game-counters>
                        </div>
                        <div class="background-div"></div>
                    </div>
                </div>

                <div class="col-lg-8 pr-0 pl-lg-4">
                    <div class="game-header white-backgrounded-div mb-sm-2 mb-lg-4">
                        <div class="game-header content-div">
                            <puzzle></puzzle>
                        </div>
                        <div class="background-div"></div>
                    </div>
                </div>
            </div>

            <div class="game-map white-backgrounded-div">
                <div class="game-map content-div">
                    <game-map></game-map>
                </div>
                <div class="background-div"></div>
            </div>
        </div>

        <div class="col-sm-12 col-lg-4 col-xl-3 pl-0">
            <div class="player-list white-backgrounded-div col-sm-12 text-left mb-sm-2 mb-lg-4">
                <div class="player-list content-div">
                    <player-list :is-in-lobby="false"></player-list>
                </div>
                <div class="background-div"></div>
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

    @Component({
        components: {Puzzle, PlayerList, Chat, GameMap, GameCounters}
    })
    export default class Game extends Vue {
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

    .game-header.white-backgrounded-div {
        height: 7vh;
    }

    .game-header.content-div {
        max-height: 7vh;
    }

    .game-map.white-backgrounded-div {
        height: 80vh;
    }

    .game-map.content-div {
        max-height: 80vh;
    }

    .player-list.white-backgrounded-div {
        height: 20vh;
    }

    .player-list.content-div {
        max-height: 20vh;
    }

    .content-div {
        width: 100%;
        overflow-y: scroll;
        position: absolute;
        z-index: 2;
        -ms-overflow-style: none;
    }

    .background-div {
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.6;
    }

    .content-div::-webkit-scrollbar {
        display: none;
    }
</style>