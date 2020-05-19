<template>
    <div v-if="!roleSelected">
        <b-button id="blue-spymaster"
                  @click="sendSelection('BLUE','SPYMASTER')"
                  :disabled="isBlueSpymasterFull"
        >Blue Spymaster
        </b-button>
        <b-button
                id="blue-spy"
                @click="sendSelection('BLUE','SPY')"
                :disabled="isBlueSpyFull"
        >Blue Spy
        </b-button>
        <b-button
                id="red-spymaster"
                @click="sendSelection('RED','SPYMASTER')"
                :disabled="isRedSpymasterFull"
        >Red Spymaster
        </b-button>
        <b-button
                id="red-spy"
                @click="sendSelection('RED','SPY')"
                :disabled="isRedSpyFull"
        >Red Spy
        </b-button>
    </div>
    <div v-else>
        <b-button id="clear-selection"
                  @click="sendSelection">
            Clear Selection
        </b-button>
    </div>

</template>

<script lang="ts">

    import {Component, Prop, Vue} from "vue-property-decorator";
    import {PlayerModel} from "@/models/playerModel";
    import {SelectionModel} from "@/models/selectionModel";
    import * as websocket from '@/services/websocket'

    @Component
    export default class RolePick extends Vue {

        private roleSelected = false;

        public sendSelection(side: string, role: string): void {
            if (this.getCurrentPlayer.role === "NOT_SELECTED" && this.getCurrentPlayer.side === "NOT_SELECTED") {
                const selection: SelectionModel = {
                    playerId: this.getCurrentPlayer.id,
                    side: side,
                    role: role,
                }
                websocket.send(process.env.VUE_APP_PLAYER_SELECTION, selection);
                this.roleSelected = true;
            } else {
                const selection: SelectionModel = {
                    playerId: this.getCurrentPlayer.id,
                    side: "NOT_SELECTED",
                    role: "NOT_SELECTED",
                }
                websocket.send(process.env.VUE_APP_PLAYER_SELECTION, selection);
                this.roleSelected = false;
            }
        }


        get getCurrentPlayer(): PlayerModel {
            return this.$store.getters["getCurrentPlayer"];
        }

        get isBlueSpymasterFull(): boolean {
            return this.$store.getters["isBlueSpymasterFull"];
        }

        get isBlueSpyFull(): boolean {
            return this.$store.getters["isBlueSpyFull"];
        }

        get isRedSpymasterFull(): boolean {
            return this.$store.getters["isRedSpymasterFull"];
        }

        get isRedSpyFull(): boolean {
            return this.$store.getters["isRedSpyFull"];
        }

    }
</script>

<style scoped>

</style>