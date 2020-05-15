<template>

    <toggle-button
            @change="sendRdyState"
            :disabled="player.name!==currentPlayer.name"
            value="false"
            :sync="true"
            v-model="player.rdyState"
            :labels="rdyLabel"
    >
    </toggle-button>
</template>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import {PlayerModel} from "@/models/playerModel";
    import {Client} from "webstomp-client";
    import {RdyModel} from "@/models/rdyModel";

    @Component
    export default class ReadyCheck extends Vue {


        @Prop()
        private player!: PlayerModel;

        @Prop()
        private stompClient!: Client;

        private isRdy = false;


      //  @Watch("player.rdyState")
        sendRdyState() {
            const rdyModel: RdyModel = {
                playerId: this.currentPlayer.id,
                rdyState: !this.currentPlayer.rdyState,
            }
            this.stompClient.send(process.env.VUE_APP_PLAYER_RDY, JSON.stringify(rdyModel));
        }

        private rdyLabel = {
            checked: "Mégsem!",
            unchecked: "Mehet a játék",
        }

        get currentPlayer(): PlayerModel {
            return this.$store.getters["getCurrentPlayer"];
        }

    }
</script>

<style scoped>

</style>