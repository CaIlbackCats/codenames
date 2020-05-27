<template>
    <div class="col-sm-12 row mx-0">
        <div class="col-sm-6">
            <p>{{puzzleWord}}</p>
        </div>
        <div class="col-sm-6">
            <p>{{puzzleNumber}}</p>
        </div>
    </div>
</template>

<script lang="ts">

    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class Puzzle extends Vue {

        get puzzleWord(): string {
            return "puzzleWorld";
        }

        get puzzleNumber(): number {
            return 8;
        }

    }
</script>

<style scoped>
    p {
        font-size: 2rem;
        color: rgb(135, 25, 75);
        font-weight: bold;
        text-shadow: 0 0 0.6rem white, 0px 0px 1.4rem rgb(255, 228, 22);
        text-align: center;
    }

</style>