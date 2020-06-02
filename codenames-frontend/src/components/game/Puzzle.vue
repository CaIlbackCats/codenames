<template>
    <div>
        <div class="col-sm-12 row mx-0" v-for="puzzle in puzzleWords" :key="puzzle.id">
            <div class="col-sm-6">
                <p>{{puzzle.puzzleWord}}</p>
            </div>
            <div class="col-sm-6">
                <p>{{puzzle.maxGuessCount}}</p>
            </div>
        </div>
    </div>

</template>

<script lang="ts">

    import {Component, Vue} from "vue-property-decorator";
    import {PuzzleWordModel} from "@/models/game/puzzleWordModel";

    @Component
    export default class Puzzle extends Vue {


        get puzzleWords(): Array<PuzzleWordModel> {
            return this.$store.getters["puzzleWords"]
        }


    }
</script>

<style scoped>
    p {
        margin-bottom: 0rem;
        font-size: 2rem;
        color: rgb(135, 25, 75);
        font-weight: bold;
        text-shadow: 0 0 0.6rem white, 0px 0px 1.4rem rgb(255, 228, 22);
        text-align: center;
    }

</style>