<template>
    <div>
        <div v-for="card in board" :key="card.id">
            <span @click="sendVote(card.id)">{{card.word.word}} - {{card.type}}</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {CardDetailsModel} from "@/models/cardDetailsModel";

    @Component
    export default class GameMap extends Vue {

        public sendVote(cardId:number):void{
            this.$store.dispatch("sendCardVote",cardId);
        }

        get board(): Array<CardDetailsModel> {
            return this.$store.getters["board"];
        }
    }
</script>

<style scoped>

</style>