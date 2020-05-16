<template >
        <b-modal size="sm"
                 hide-footer
                 hide-header
                 @hide="hidePopPup"
                 id="kick-modal"
                 centered>
                <div class="text-center">
                    <p>Do you want to kick <span style="font-weight: bold">{{playerToKick.name}}</span>?</p>
                    <button class="yes" @click="kickPlayer(true)">
                        <font-awesome-icon style="font-size: 2rem; margin-right: 2rem" icon="check"/></button>
                    <button class="no" @click="kickPlayer(false)">
                        <font-awesome-icon style="font-size: 2rem" icon="times"/></button>
                    <p>{{counter}}</p>
                </div>
        </b-modal>
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
                this.$bvModal.show("kick-modal");
                this.timer = setInterval(() => this.decreaseCounter(), MILISEC);
            } else {
                this.$bvModal.hide("kick-modal");
                clearInterval(this.timer);
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
            this.updatePlayerRemovalInfo(this.kickInitPlayer.id, this.playerToKick.id, false).then(() => {
                if (this.playerRemovalInfo.ownerId === this.kickInitPlayer.id) {
                    this.stompClient.send(process.env.VUE_APP_PLAYER_KICK, JSON.stringify(this.playerRemovalInfo));
                }
            });
        }

        public kickPlayer(vote: boolean): void {
            if (this.playerRemovalInfo.ownerId == this.kickInitPlayer.id && this.kickInitPlayer.lobbyOwner) {
                clearInterval(this.timer);
                this.updatePlayerRemovalInfo(this.kickInitPlayer.id, this.playerToKick.id, vote).then(() => {
                    this.stompClient.send(process.env.VUE_APP_PLAYER_KICK, JSON.stringify(this.playerRemovalInfo));
                });
            } else {
                this.updatePlayerRemovalInfo(this.kickInitPlayer.id, this.playerToKick.id, vote).then(() => {
                    this.stompClient.send(process.env.VUE_APP_PLAYER_KICK_COUNT, JSON.stringify(this.playerRemovalInfo));
                });
            }
            this.hidePopPup();
        }

        private async updatePlayerRemovalInfo(ownerId: number, playerToRemoveId: number, vote: boolean): Promise<void> {
            const playerRemovalModel: PlayerRemovalModel = {
                ownerId: ownerId,
                playerToRemoveId: playerToRemoveId,
                vote: vote,
            }
            return this.$store.dispatch("updatePlayerRemovalInfo", playerRemovalModel);
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
    button {
        background-color: transparent;
        border: none;
        box-shadow: none;
        outline: none;
        color: gray;
    }
    .yes:hover{
        color: lightgreen;
    }
    .no:hover{
        color: rgb(135, 25, 75);
    }


</style>