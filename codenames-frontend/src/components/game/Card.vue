<template>
  <div
    @click="sendVote(card.id)"
    :class="[
      isBlue ? 'blue-card' : '',
      isRed ? 'red-card' : '',
      isBlack ? 'black-card' : '',
      isGold ? 'gold-card' : '',
      card.isFound ? 'found' : '',
      'card'
    ]"
  >
    <label class="m-0">{{ card.word.word }} </label>
    <div class="card-line"></div>
  </div>
</template>

<script lang="ts">
<<<<<<< HEAD
    import {Component, Prop, Vue} from "vue-property-decorator";
    import {TypedCardDetailsModel} from "../../models/game/card/typedCardDetailsModel";

    @Component
    export default class Card extends Vue {

        private isBlue = false;
        private isRed = false;
        private isBlack = false;
        private isGold = false;

        @Prop()
        private card!: TypedCardDetailsModel;

        mounted() {
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

=======
import { Component, Prop, Vue } from "vue-property-decorator";
import { CardDetailsModel } from "../../models/game/card/cardDetailsModel";

@Component
export default class Card extends Vue {
  private isBlue = false;
  private isRed = false;
  private isBlack = false;
  private isGold = false;

  @Prop()
  private card!: CardDetailsModel;

  mounted() {
    switch (this.card.type) {
      case "BLUE_SPY":
        this.isBlue = true;
        break;
      case "RED_SPY":
        this.isRed = true;
        break;
      case "BYSTANDER":
        this.isGold = true;
        break;
      case "ASSASSIN":
        this.isBlack = true;
        break;
>>>>>>> set hungarian language option with i18n
    }
  }

  public sendVote(cardId: number): void {
    if (this.currentTeam === this.currentPlayerSide) {
      this.$store.dispatch("sendCardVote", cardId);
    }
  }

  get currentPlayerSide(): string {
    return this.$store.getters["currentPlayerSide"];
  }

  get currentTeam(): string {
    return this.$store.getters["currentTeam"];
  }
}
</script>

<style scoped>
.card-line {
  height: 1px;
  background: rgb(255, 255, 255);
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 10%,
    rgba(255, 255, 255, 0.4) 50%,
    rgba(255, 255, 255, 0) 90%
  );
}

.card {
  height: 12vh;
  border-radius: 1rem;
  color: white;
  font-weight: bold;
  text-transform: uppercase;
  font-size: 1.3rem;
  padding-top: 3vh;
  opacity: 0.4;
}

.found {
  opacity: 1;
}

.blue-card {
  background-color: rgba(30, 144, 255, 0.8);
}

.red-card {
  background-color: rgba(205, 92, 92, 0.8);
}

.black-card {
  background-color: rgba(0, 0, 0, 0.8);
}

.gold-card {
  background-color: rgba(184, 134, 11, 0.8);
}
</style>
