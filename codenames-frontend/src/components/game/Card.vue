<template>
    <div @click="sendVote(card.id)" :class="[isBlue ? 'blue-card' : '',
        isRed ? 'red-card' : '',
        isBlack ? 'black-card' : '',
        isGold ? 'gold-card': '',
        card.isFound ? 'found' : '',
        'card']">
        <label class="m-0">{{card.word.word}} </label>
        <div :class="[isRed || isBlack || isGold || isBlue ? 'white-line' : 'pink-line']"></div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import {TypedCardDetailsModel} from "@/models/game/card/typedCardDetailsModel";
    import {TypelessCardDetailsModel} from "@/models/game/card/typelessCardDetailsModel";

    @Component
    export default class Card extends Vue {

        private isBlue = false;
        private isRed = false;
        private isBlack = false;
        private isGold = false;

        @Prop()
        private card!: TypedCardDetailsModel | TypelessCardDetailsModel;

        @Watch("card.type", {immediate: true})
        private updateCardType(): void {
            switch (this.card.type) {
                case 'BLUE_SPY':
                    this.isBlue = true;
                    break;
                case 'RED_SPY':
                    this.isRed = true;
                    break;
                case 'BYSTANDER':
                    this.isGold = true;
                    break;
                case 'ASSASSIN':
                    this.isBlack = true;
                    break;
            }
        }

        public sendVote(cardId: number): void {
            if (this.activeTurn && this.isCurrentPlayerSpy) {
                this.$store.dispatch("sendCardVote", cardId);
            }
        }

        get activeTurn(): boolean {
            const currentPlayerActiveTurn: boolean = this.$store.getters["isCurrentPlayerActiveTurn"]
            const currentTeamActiveTurn: boolean = this.$store.getters["isCurrentTeamActive"];
            return currentPlayerActiveTurn && currentTeamActiveTurn;
        }

        get isCurrentPlayerSpy(): boolean {
            const currentPlayerRole: string = this.$store.getters["currentPlayerRole"];
            return currentPlayerRole === "SPY";
        }

    }
</script>

<style scoped>

    .white-line {
        height: 1px;
        background: rgb(255, 255, 255);
        background: linear-gradient(90deg, rgba(255, 255, 255, 0) 10%, rgba(255, 255, 255, 0.4) 50%, rgba(255, 255, 255, 0) 90%);
    }


    .pink-line {
        height: 1px;
        background: rgb(135, 25, 75);
        background: linear-gradient(90deg, rgba(135, 25, 75, 0) 10%, rgba(135, 25, 75, 0.4) 50%, rgba(135, 25, 75, 0) 90%);
    }

    .card {
        height: 12vh;
        border-radius: 1rem;
        color: rgb(135, 25, 75);
        font-weight: bold;
        text-transform: uppercase;
        font-size: 1.3rem;
        padding-top: 3vh;
        opacity: 0.6;
    }

    .found {
        opacity: 1;
    }

    .blue-card {
        background-color: rgba(30, 144, 255, 0.8);
        color: white;
    }

    .red-card {
        background-color: rgba(205, 92, 92, 0.8);
        color: white;
    }

    .black-card {
        background-color: rgba(0, 0, 0, 0.8);
        color: white;
    }

    .gold-card {
        background-color: rgba(184, 134, 11, 0.8);
        color: white;
    }

</style>