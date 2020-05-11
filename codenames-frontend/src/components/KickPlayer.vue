<template>
    <modal @before-close="hidePopPup" name="kick-modal">
        <p v-if="kickInitPlayer.lobbyOwner">Biztos ki akarod dobni?: {{playerToKick.name}}</p>
        <p v-if="!kickInitPlayer.lobbyOwner">Biztos ki akarod rúgatni?: {{playerToKick.name}}</p>
        <button @click="kickPlayer(true)">Igen</button>
        <button @click="kickPlayer(false)">Nem</button>
        <p>{{counter}}</p>
    </modal>
</template>

<script lang="ts">

    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import {PlayerModel} from "@/models/playerModel";
    import {Client} from "webstomp-client";
    import {PlayerRemovalModel} from "@/models/playerRemovalModel";

    const MAX_VOTE_TIME = 15;
    const MILISEC = 1000;

    @Component
    export default class KickPlayer extends Vue {

        @Prop()
        private playerToKick!: PlayerModel;

        @Prop()
        private kickInitPlayer!: PlayerModel;

        @Prop()
        private stompClient!: Client;

        private counter = MAX_VOTE_TIME;

        private timer;

        @Watch("kickWindow")
        private showModal() {
            if (this.kickWindow) {
                this.counter = MAX_VOTE_TIME;
                this.$modal.show("kick-modal");
                this.timer = setInterval(() => this.decreaseCounter(), MILISEC);
            } else {
                this.$modal.hide("kick-modal");
            }
        }

        @Watch("counter")
        private sendMsg() {
            if (this.counter == 0) {
                this.hidePopPup()
                this.sendKickMsg();
            }
        }

        constructor() {
            super();
        }

        private sendKickMsg() {
            clearInterval(this.timer);
            if (this.kickInitPlayer.lobbyOwner) {
                this.ownerKick(false);
            } else {
                const playerRemovalModel: PlayerRemovalModel = {
                    playerToRemoveId: this.playerToKick.id,
                    vote: true,
                }
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_BY_VOTE, JSON.stringify(playerRemovalModel));
            }
        }

        public hidePopPup(): void {
            if (this.kickWindow) {
                this.$store.dispatch("showKickWindow", false);
            }
        }

        public kickPlayer(vote: boolean): void {
            if (this.kickInitPlayer.lobbyOwner) {
                this.ownerKick(vote);
                clearInterval(this.timer);
            } else {
                const playerRemovalModel: PlayerRemovalModel = {
                    playerToRemoveId: this.playerToKick.id,
                    vote: vote,
                }
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_COUNT, JSON.stringify(playerRemovalModel));
            }
            this.hidePopPup();
        }

        private ownerKick(vote: boolean): void {
            const playerRemovalModel: PlayerRemovalModel = {
                ownerId: this.kickInitPlayer.id,
                playerToRemoveId: this.playerToKick.id,
                vote: vote,
            }
            this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_BY_OWNER, JSON.stringify(playerRemovalModel));
        }

        private decreaseCounter(): void {
            if (this.counter != 0) {
                this.counter -= 1;
            }
        }


        get kickWindow(): boolean {
            return this.$store.getters["getInitKickWindow"]
        }


    }
</script>

<style scoped>

</style>