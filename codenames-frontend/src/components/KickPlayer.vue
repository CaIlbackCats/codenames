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
                clearInterval(this.timer);
                this.hidePopPup()
                this.sendKickMsg();
            }
        }

        constructor() {
            super();
        }

        private sendKickMsg() {
            //  this.updatePlayerRemovalInfo(this.kickInitPlayer.id, this.playerToKick.id, false);
            if (this.playerRemovalInfo.ownerId == -1) {
                this.playerRemovalInfo.ownerId = this.kickInitPlayer.id;
            }
            if (this.playerRemovalInfo.playerToRemoveId == -1) {
                this.playerRemovalInfo.playerToRemoveId = this.playerToKick.id;
            }
            this.playerRemovalInfo.vote = false;
            if (this.playerRemovalInfo.ownerId === this.kickInitPlayer.id)
            {
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK, JSON.stringify(this.playerRemovalInfo));
            }
        }

        public kickPlayer(vote: boolean): void {
            if (this.kickInitPlayer.lobbyOwner) {
                clearInterval(this.timer);
                // this.updatePlayerRemovalInfo(this.kickInitPlayer.id, this.playerToKick.id, vote);
                if (this.playerRemovalInfo.ownerId == -1) {
                    this.playerRemovalInfo.ownerId = this.kickInitPlayer.id;
                }
                if (this.playerRemovalInfo.playerToRemoveId == -1) {
                    this.playerRemovalInfo.playerToRemoveId = this.playerToKick.id;
                }
                this.playerRemovalInfo.vote = vote;
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK, JSON.stringify(this.playerRemovalInfo));
            } else {
                //  this.updatePlayerRemovalInfo(this.kickInitPlayer.id, this.playerToKick.id, vote);
                if (this.playerRemovalInfo.ownerId == -1) {
                    this.playerRemovalInfo.ownerId = this.kickInitPlayer.id;
                }
                if (this.playerRemovalInfo.playerToRemoveId == -1) {
                    this.playerRemovalInfo.playerToRemoveId = this.playerToKick.id;
                }
                this.playerRemovalInfo.vote = vote;
                this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_COUNT, JSON.stringify(this.playerRemovalInfo));
            }
            this.hidePopPup();
        }

        private updatePlayerRemovalInfo(ownerId: number, playerToRemoveId: number, vote: boolean): void {
            const playerRemovalModel: PlayerRemovalModel = {
                ownerId: ownerId,
                playerToRemoveId: playerToRemoveId,
                vote: vote,
            }
            this.$store.dispatch("updatePlayerRemovalInfo", playerRemovalModel);
        }

        public hidePopPup(): void {
            if (this.kickWindow) {
                this.$store.dispatch("showKickWindow", false);
            }
        }

        private decreaseCounter(): void {
            if (this.counter != 0) {
                this.counter -= 1;
            }
        }

        get kickWindow(): boolean {
            return this.$store.getters["getInitKickWindow"];
        }

        get playerRemovalInfo(): PlayerRemovalModel {
            return this.$store.getters["getPlayerRemovalInfo"];
        }


    }
</script>

<style scoped>

</style>