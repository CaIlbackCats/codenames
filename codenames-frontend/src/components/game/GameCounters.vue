<template>
    <div class="row">
        <div class="col-sm-3">
            <span class="blue">{{blueScore}}</span>
        </div>
        <div class="col-sm-3">
            <span class="red">{{redScore}}</span>
        </div>
        <div class="col-sm-3">
            <span class="gold">{{civilians}}</span>
        </div>
        <div class="col-sm-3">
            <span class="rounds">{{rounds}}</span>
        </div>
    </div>
</template>

<script lang="ts">

    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class GameCounters extends Vue {

        get blueScore(): number {
            return this.$store.getters['blueTeamScore'];
        }

        get redScore(): number {
            return this.$store.getters['redTeamScore'];
        }

        get rounds(): number {
            return this.$store.getters['rounds'];
        }

        get civilians(): number {
            return this.$store.getters['civilians']!;
        }

    }
</script>

<style scoped>
    span {
        font-size: 2rem;
        font-weight: bold;
    }

    span.blue {
        color: dodgerblue;
    }

    span.red {
        color: indianred;
    }

    span.gold {
        color: darkgoldenrod;
    }

    span.rounds {
        color: rgb(135, 25, 75);
    }
</style>