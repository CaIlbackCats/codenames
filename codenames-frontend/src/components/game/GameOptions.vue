<template>
    <div :class="['game-options', {'move-right':activeTurn}]">
        <div v-if="!currentPlayerSpymaster">
            <p class="timer" v-if="isVoteOn">{{counter}}</p>
            <b-button @click="sendPassTurn" squared
            >
                {{ $t("game-option.vote") }}
            </b-button>
            <p>{{passVoteCounter}}/{{currentTeamSize}}</p>
        </div>

        <div class="col-sm-8 col-lg-6 offset-sm-2 offset-lg-3" v-else>
            <b-input required
                     size="md"
                     style="margin-bottom: 1rem"
                     maxlength="30"
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
                              :disabled="!isPuzzleWordValid"
                    >Ok
                    </b-button>
                </b-input-group-append>
            </b-input-group>
        </div>
        <img :src="spyGameUrl" alt="spy">
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {PuzzleWordModel} from "@/models/game/puzzleWordModel";
    import {TypedCardDetailsModel} from "@/models/game/card/typedCardDetailsModel";
    import {TypelessCardDetailsModel} from "@/models/game/card/typelessCardDetailsModel";

    const MAX_VOTE_TIME = 5;
    const MILISEC = 1000;

    @Component
    export default class GameOptions extends Vue {

        private spyGameUrl = require("../../assets/spy_game.png");

        private puzzleWord = "";
        private isPuzzleWordValid = false;

        private maxGuessCount = 0;

        private counter = MAX_VOTE_TIME;
        private timer = 0;
        private voting = false;


        @Watch("puzzleWord")
        private validatePuzzleWord() {
            this.isPuzzleWordValid = this.puzzleWord !== "";
        }

        get isVoteOn() {
            return this.board.find(card => card.voted)
        }

        get board(): Array<TypedCardDetailsModel> | Array<TypelessCardDetailsModel> {
            return this.$store.getters['board'];
        }

        public sendPuzzleWord() {

            const puzzleWordModel: PuzzleWordModel = {
                id: -1,
                puzzleWord: this.puzzleWord,
                maxGuessCount: this.maxGuessCount,
                usedGuesses: 0,
                wordRegisterTime: new Date(),
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

        @Watch("isVoteOn")
        private showTimer() {
            if (this.isVoteOn && this.timer === 0) {
                this.counter = MAX_VOTE_TIME;
                this.voting = true;
                this.timer = setInterval(() => this.decreaseCounter(), MILISEC)
            }
        }

        private decreaseCounter(): void {
            if (this.counter != 0) {
                this.counter -= 1;
            } else {
                clearInterval(this.timer);
                this.voting = false;
                this.timer = 0;
            }
        }
    }
</script>

<style scoped>

    p {
        color: white;
        font-size: 1rem;
    }

    button {
        background-color: rgb(135, 25, 75);
        border: 0 solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
        outline: rgba(250, 230, 15, .5) solid 1px;
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
        margin-top: 2rem;
        width: 25vw;
    }

    .timer {
        font-size: 2rem;
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