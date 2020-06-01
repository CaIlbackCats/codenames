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
                               v-if="player.name !== currentPlayerName && isInLobby"/>
            <KickPlayer v-if="isInLobby"></KickPlayer>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import KickPlayer from "@/components/player/KickPlayer.vue";
    import {PlayerModel} from "@/models/player/playerModel";
    import ReadyCheck from "@/components/player/ReadyCheck.vue";
    import router from "@/router";

    @Component({
        components: {KickPlayer, ReadyCheck}
    })

    export default class PlayerList extends Vue {
        @Prop()
        private isInLobby!: boolean;

        constructor() {
            super();
            window.addEventListener('beforeunload', this.hideLeftPlayer);
        }

        async mounted() {
            //await this.$store.dispatch("checkSelectedPlayer", {root: true});
        }

        @Watch("currentPlayerId", {immediate: true})
        private setPlayer() {
            if (this.currentPlayerId !== -1) {
                //localStorage.setItem('currentPlayerId', JSON.stringify(this.currentPlayerId));
                this.$store.dispatch("subscribeToPlayerChange");
            } else if (this.currentPlayerId === -1) {
                router.push("/");
            }
        }

        private hideLeftPlayer(): void {
            this.$store.dispatch("hideLeftPlayer");
        }

        get players(): Array<PlayerModel> {
            return this.$store.getters["playersOrdered"];
        }

        get currentPlayerId(): number {
            return this.$store.getters["currentPlayerId"];
        }


        get currentPlayerName(): string {
            return this.$store.getters["currentPlayerName"]
        }

        public initKickPlayer(playerToRemoveId: number): void {
            //todo :disabled to fontawesome icon
            if (!this.isKickingPhase) {
                this.$store.dispatch("sendKickWindowInit", playerToRemoveId);
            }
        }

        get isKickingPhase(): boolean {
            return this.$store.getters["isKickingPhase"];
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