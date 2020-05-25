<template>
    <b-modal size="sm"
             hide-footer
             hide-header
             @hide="hidePopPup"
             id="kick-modal"
             centered>
        <div class="text-center">
            <p>Do you want to kick <span style="font-weight: bold">{{playerToKickName}}</span>?</p>
            <button class="yes" @click="kickPlayer(true)">
                <font-awesome-icon style="font-size: 2rem; margin-right: 2rem" icon="check"/>
            </button>
            <button class="no" @click="kickPlayer(false)">
                <font-awesome-icon style="font-size: 2rem" icon="times"/>
            </button>
            <p>{{counter}}</p>
        </div>
    </b-modal>
</template>

<script lang="ts">

    import {Component, Vue, Watch} from "vue-property-decorator";

    const MAX_VOTE_TIME = 15;
    const MILISEC = 1000;

    @Component
    export default class KickPlayer extends Vue {

        private counter = MAX_VOTE_TIME;

        private timer! : number;

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
                this.hidePopPup();
                this.$store.dispatch("sendKick");
            }
        }

        constructor() {
            super();
        }

        public kickPlayer(vote: boolean): void {
            this.$store.dispatch("sendVote", vote);
            clearInterval(this.timer);
            this.hidePopPup();
        }

        public hidePopPup(): void {
            if (this.kickWindow) {
                this.$store.dispatch("setKickWindow", false);
            }
        }

        private decreaseCounter(): void {
            if (this.counter != 0) {
                this.counter -= 1;
            }
        }

        get kickWindow(): boolean {
            return this.$store.getters["isKickWindow"];
        }

        get playerToKickName():string{
          return this.$store.getters["playerToKickName"];
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

    .yes:hover {
        color: lightgreen;
    }

    .no:hover {
        color: rgb(135, 25, 75);
    }


</style>