<template>
    <div class="game-end-div">
        <div class="end-header">
            <img :class="{'move-down': moveLabel}" :src="logoUrl">
        </div>
        <div class="white-backgrounded-div stats-div col-sm-12 col-lg-6 offset-3 text-left p-lg-4">
            <span>Number of </span>
            <hr>
            <div class="row mx-0">
                <div class="col-lg-5 offset-lg-1">
                    <p>civilians</p>
                    <p>enemy spies</p>
                    <p>friendly spies</p>
                    <p>rounds</p>
                    <p>passes</p>
                    <p>invalid votes</p>
                </div>
                <div class="col-lg-5 text-right">
                    <p>{{teamStatistics.numOfCivilians}}</p>
                    <p>{{teamStatistics.numOfEnemySpies}}</p>
                    <p>{{score}}</p>
                    <p>{{teamStatistics.teamRounds}}</p>
                    <p>{{teamStatistics.numOfPasses}}</p>
                    <p>{{teamStatistics.numOfInvalidVotes}}</p>
                </div>

                <div class="col-lg-5 offset-lg-1 mt-lg-4">
                    <p><span>Best round</span></p>
                    <p><span>Worst round</span></p>
                    <p><span>Team accuracy</span></p>
                </div>
                <div class="col-lg-5 text-right mt-lg-4">
                    <p><span>0 spies in a row</span></p>
                    <p><span>0 spies in a row</span></p>
                    <p><span>0%</span></p>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {StatModel} from "@/models/game/statModel";
    @Component
    export default class GameEnd extends Vue {
        private logoUrl = "";
        // private loseUrl = require("../../assets/maybe_next_time.png");
        // private winUrl = require("../../assets/you_won.png");
        private moveLabel = false;
        get currentPlayerSide(): string {
            return this.$store.getters['currentPlayerSide'];
        }
        moveDown() {
            this.moveLabel = true;
        }
        get currentTeam(): string {
            return this.$store.getters['currentTeam'];
        }
        mounted() {
            this.currentTeam === this.currentPlayerSide ? this.logoUrl = this.winUrl : this.logoUrl = this.loseUrl;
            setTimeout(() => this.moveDown(), 500);
        }
        get teamStatistics(): StatModel {
            return this.$store.getters['currentPlayerTeamStatistics'];
        }
        get score(): number {
            return this.$store.getters['ownScore'];
        }
    }
</script>

<style scoped>
    span {
        font-size: 0.8rem;
        font-weight: bold;
        color: rgb(135, 25, 75);
        text-transform: uppercase;
    }
    p {
        margin: 0;
        color: rgba(135, 25, 75, 0.7);
        font-size: 1rem;
    }
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