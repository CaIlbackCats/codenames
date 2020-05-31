<template>
    <div class="row game-map offset-lg-1">
        <div class="col-sm-12 col-lg-2 m-lg-2 px-0" v-for="card in board" :key="card.id">
            <card :card="card"></card>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {TypedCardDetailsModel} from "@/models/game/card/typedCardDetailsModel";
    import Card from "@/components/game/Card.vue";
    @Component({
        components: {Card}
    })
    export default class GameMap extends Vue {

        get board(): Array<TypedCardDetailsModel> {
            return this.$store.getters["board"];
        }

    }
</script>

<style scoped>

    .game-map {
        padding-top: 2vh;
    }

</style>