<template>
    <div class="game-options">
        <b-button squared
                  v-if="!spyMaster"
        >No Vote
        </b-button>
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
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import {PuzzleWordModel} from "@/models/game/puzzleWordModel";

    @Component
    export default class GameOptions extends Vue {

        @Prop()
        private spyMaster!: boolean;

        private puzzleWord = "";

        private maxGuessCount = 0;

        public sendPuzzleWord() {
            const puzzleWordModel: PuzzleWordModel = {
                id: -1,
                puzzleWord: this.puzzleWord,
                maxGuessCount: this.maxGuessCount,
            }
            this.$store.dispatch("sendPuzzleWord", puzzleWordModel);
        }

    }
</script>

<style scoped>
    .game-options {
        width: 100%;
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
</style>