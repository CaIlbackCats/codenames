<template>
    <div :class="['game-options', {'move-right':activeTurn}]">
        <div v-if="!currentPlayerSpymaster">
            <b-button @click="sendPassTurn" squared
            >No Vote
            </b-button>
            {{passVoteCounter}}/{{currentTeamSize}}
        </div>

        <div class="col-sm-8 col-lg-6 offset-sm-2 offset-lg-3" v-else>
            <b-input required
                     size="md"
                     style="margin-bottom: 1rem"
                     type="text"
                     v-model="puzzleWord"
            ></b-input>
            <b-input-group size="md">
                <b-form-input
                        min="0"
                        max="25"
                        required
                        type="number"
                        v-model="maxGuessCount"
                ></b-form-input>
                <b-input-group-append>
                    <b-button squared
                              type="submit"
                              @click="sendPuzzleWord"
                    >Ok
                    </b-button>
                </b-input-group-append>
            </b-input-group>
        </div>
        <img :src="spyGameUrl" alt="spy">
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {PuzzleWordModel} from "@/models/game/puzzleWordModel";


    @Component
    export default class GameOptions extends Vue {

        private spyGameUrl = require("../../assets/spy_game.png");

        private puzzleWord = "";

        private maxGuessCount = 0;

        public sendPuzzleWord() {

            const puzzleWordModel: PuzzleWordModel = {
                id: -1,
                puzzleWord: this.puzzleWord,
                maxGuessCount: this.maxGuessCount,
                usedGuesses: 0,
            }
            this.$store.dispatch("sendPuzzleWord", puzzleWordModel);
        }

        private sendPassTurn(): void {
            this.$store.dispatch("sendPassVote");
        }

        get passVoteCounter(): number {
            return this.$store.getters["passVoteCounter"];
        }

        get currentTeamSize(): number {
            return this.$store.getters["currentTeamSize"];
        }

        get currentPlayerSpymaster(): boolean {
            return this.$store.getters["isCurrentPlayerSpymaster"];
        }

        get activeTurn(): boolean {
            const currentPlayerActiveTurn: boolean = this.$store.getters["isCurrentPlayerActiveTurn"]
            const currentTeamActiveTurn: boolean = this.$store.getters["isCurrentTeamActive"];
            return currentPlayerActiveTurn && currentTeamActiveTurn;
        }

    }
</script>

<style scoped>

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

    input {
        text-align: center;
        opacity: 0.6;
        outline: none;
        box-shadow: none;
        border: none;
    }

    input:focus {
        opacity: 1;
        outline: none;
        box-shadow: none;
        border: none;
    }

    img {
        margin-top: 4rem;
        width: 25vw;
    }

    .game-options {
        position: absolute;
        bottom: 0;
        left: -30vw;
        transition: all 1s;
    }

    .game-options.move-right {
        left: -5vw;
    }
</style>