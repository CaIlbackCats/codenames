<template>
    <div>
        <div v-for="card in board" :key="card.id">
            <span @click="sendVote">{{card.word.word}} - {{card.type}}</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {CardDetailsModel} from "@/models/game/card/cardDetailsModel";

    @Component
    export default class GameMap extends Vue {

        public sendVote():void{
            this.$store.dispatch("sendGameState");
        }

        get board(): Array<CardDetailsModel> {
            return this.$store.getters["board"];
        }
    }
</script>

<style scoped>

</style>