<template>
    <div class="game">
        <div class="white-backgrounded-div col-sm-12 col-lg-8">
            <div class="content-div">
                <game-counters></game-counters>
            </div>
            <div class="background-div"></div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import GameCounters from "@/components/game/GameCounters.vue";

    @Component({
        components: {GameCounters}
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

    .background-div {
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.6;
    }

    .white-backgrounded-div {
        height: 7vh;
    }

    .content-div {
        width: 100%;
        max-height: 5vh;
        overflow-y: scroll;
        position: absolute;
        z-index: 2;
        -ms-overflow-style: none;
    }

    .content-div::-webkit-scrollbar {
        display: none;
    }
</style>