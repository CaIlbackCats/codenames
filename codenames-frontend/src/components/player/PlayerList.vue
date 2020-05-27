<template>
    <div>
        <div :key="player.id" class="m-2"
             v-for="player in players">
            <ReadyCheck :player="player" v-if="isInLobby"></ReadyCheck>
            <font-awesome-icon class="mr-2" icon="user-secret"
                               v-if="player.id === currentPlayerId"/>
            <font-awesome-icon :class="[ 'mr-2', player.side === 'BLUE' ? 'blue-spymaster': 'red-spymaster'] "
                               :icon="['fas', 'briefcase']"
                               v-if="player.role === 'SPYMASTER'"
            ></font-awesome-icon>
            <label :style="player.side === 'BLUE' ? 'color: dodgerblue':
                                                        player.side === 'RED' ? 'color: indianred' : 'color: #87194B' "
                   class="mr-2">
                {{player.name}}</label>
            <font-awesome-icon @click="initKickPlayer(player.id)"
                               class="kick-btn"
                               icon="user-minus"
                               v-if="player.name !== currentPlayerName"/>
            <KickPlayer></KickPlayer>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import KickPlayer from "@/components/player/KickPlayer.vue";
    import {PlayerModel} from "@/models/playerModel";
    import {PlayerRemovalModel} from "@/models/playerRemovalModel";
    import * as websocket from "@/services/websocket";
    import ReadyCheck from "@/components/player/ReadyCheck.vue";

    @Component({
        components: {KickPlayer, ReadyCheck}
    })

    export default class PlayerList extends Vue {
        @Prop()
        private isInLobby!: boolean;

        get players(): Array<PlayerModel> {
            return this.$store.getters["playersOrdered"];
        }

        get currentPlayerId() : number{
            return this.$store.getters["currentPlayerId"];
        }


        get currentPlayerName(): string {
            return this.$store.getters["currentPlayerName"]
        }

        public initKickPlayer(playerToRemoveId: number): void {
            this.$store.dispatch("sendKickWindowInit", playerToRemoveId);
        }

    }
</script>

<style scoped>
    svg {
        color: rgb(135, 25, 75);
    }

    .blue-spymaster {
        color: dodgerblue;
    }

    .red-spymaster {
        color: indianred;
    }

    .kick-btn {
        opacity: 0.2;
    }

    .kick-btn:hover {
        opacity: 0.8;
    }
</style>