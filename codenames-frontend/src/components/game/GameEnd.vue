<template>
    <div class="game-end-div">
        <div class="end-header">
            <img :class="{'move-down': moveLabel}" :src="logoUrl">
        </div>
        <div class="white-backgrounded-div stats-div col-sm-12 col-lg-6 offset-3">
            <p>Number of </p>
            <p>civilians</p>
            <p>enemy spy</p>
            <p>rounds</p>
            <p>passes</p>
            <p>Best round</p>
            <p>Worst round</p>
            <p>Team accuracy</p>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class GameEnd extends Vue {
        private logoUrl = "";
        private loseUrl = require("../../assets/maybe_next_time.png");
        private winUrl = require("../../assets/you_won.png");
        private moveLabel = false;
        private isInWinnerTeam = false;

        mounted() {
            this.isInWinnerTeam ? this.logoUrl = this.winUrl : this.logoUrl = this.loseUrl;
            setTimeout(() => this.moveDown(), 500);
        }

        moveDown() {
            this.moveLabel = true;
        }
    }
</script>

<style scoped>
    img {
        margin-top: -10rem;
        max-width: 60%;
        max-height: 15vh;
        transition: 0.5s;
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

    .move-down {
        margin-top: 4rem;
    }

    .stats-div {
        margin-top: 10vh;
        height: 50vh;
    }

    .end-header {
        width: 100%;
    }

    .game-end-div {
        width: 100%;
    }
</style>