<template>
    <button
            @click="sendRdyState"
            :disabled="player.name!==currentPlayer.name"
            :pressed.sync="player.rdyState"
            :class="['rdy-btn mr-2', player.rdyState ? 'is-ready' : '']"
    ><font-awesome-icon icon="check"/>
    </button>
</template>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import {PlayerModel} from "@/models/playerModel";
    import {RdyModel} from "@/models/rdyModel";
    import * as websocket from '@/services/websocket'
    import {config} from "@/config";

    @Component
    export default class ReadyCheck extends Vue {
        @Prop()
        private player!: PlayerModel;

        sendRdyState() {
            const rdyModel: RdyModel = {
                playerId: this.currentPlayer.id,
                rdyState: !this.currentPlayer.rdyState,
            }
            websocket.send(config.PLAYER_SET_READY_PATH, rdyModel);
        }

        get currentPlayer(): PlayerModel {
            return this.$store.getters["getCurrentPlayer"];
        }
    }
</script>

<style scoped>
    button {
        all: unset;
    }
    button:disabled{
        all: unset;
        pointer-events: none;
        opacity: .4;
    }
    button:active, button:hover, button:focus{
        all: unset;
    }
    .rdy-btn{
        all: unset;
        cursor: pointer;
    }
    .rdy-btn:hover:enabled{
        all: unset;
        cursor: pointer;
        color: lightgreen;
    }
    .is-ready{
        all: unset;
        cursor: pointer;
        color: lightgreen !important;
    }

</style>