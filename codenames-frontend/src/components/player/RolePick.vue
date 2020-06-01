<template>
  <div class="role-picker-div">
    <div class="role-picker">
      <div v-if="isRoleSelected">
        <font-awesome-icon
          title="blue spymaster"
          style="color: dodgerblue"
          id="blue-spymaster"
          @click="sendSelection('BLUE', 'SPYMASTER')"
          v-if="!isBlueSpymasterFull"
          :icon="['fas', 'briefcase']"
        >
        </font-awesome-icon>
        <font-awesome-icon
          title="blue spy"
          style="color: dodgerblue"
          id="blue-spy"
          @click="sendSelection('BLUE', 'SPY')"
          v-if="!isBlueSpyFull"
          :icon="['fas', 'user-secret']"
        >
        </font-awesome-icon>
        <font-awesome-icon
          title="red spymaster"
          style="color: indianred"
          id="red-spymaster"
          @click="sendSelection('RED', 'SPYMASTER')"
          v-if="!isRedSpymasterFull"
          :icon="['fas', 'briefcase']"
        >
        </font-awesome-icon>
        <font-awesome-icon
          title="red spy"
          style="color: indianred"
          id="red-spy"
          @click="sendSelection('RED', 'SPY')"
          v-if="!isRedSpyFull"
          :icon="['fas', 'user-secret']"
        >
        </font-awesome-icon>
      </div>
      <div v-else>
        <b-button id="clear-selection" size="sm" squared @click="sendSelection">
          {{$t("role-pick.clear")}}
<!--          Clear Selection-->
        </b-button>
      </div>
    </div>
    <div class="role-picker-background-div"></div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { SelectionModel } from "@/models/lobby/selectionModel";

@Component
export default class RolePick extends Vue {
  public sendSelection(side: string, role: string): void {
    if (
      this.currentPlayerRole !== "NOT_SELECTED" &&
      this.currentPlayerSide !== "NOT_SELECTED"
    ) {
      side = "NOT_SELECTED";
      role = "NOT_SELECTED";
    }
    const selectionModel: SelectionModel = {
      playerId: this.currentPlayerId,
      side: side,
      role: role
    };
    this.$store.dispatch("sendSelection", selectionModel);
  }

  get currentPlayerRole(): string {
    return this.$store.getters["currentPlayerRole"];
  }

  get currentPlayerSide(): string {
    return this.$store.getters["currentPlayerSide"];
  }

  get isRoleSelected(): boolean {
    return this.$store.getters["isRoleSelected"];
  }

  get currentPlayerId(): number {
    return this.$store.getters["currentPlayerId"];
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
button {
  margin: 1.5rem;
}

svg {
  font-size: 3rem;
  box-sizing: content-box;
  -webkit-filter: drop-shadow(1px 1px 1px rgba(0, 0, 0, 0.5));
  filter: drop-shadow(1px 1px 1px rgba(0, 0, 0, 0.5));
  margin: 0.5rem 1rem 0.5rem 1rem;
}
svg:hover {
  cursor: pointer;
  -webkit-filter: drop-shadow(1px 1px 1px rgba(255, 255, 255, 0.7));
  filter: drop-shadow(1px 1px 1px rgba(255, 255, 255, 0.7));
}

.role-picker-div {
  height: 10vh;
  position: relative;
}

.role-picker {
  height: 100%;
  width: 100%;
  position: absolute;
  z-index: 1;
}

.role-picker-background-div {
  height: 100%;
  width: 100%;
  background-color: white;
  position: relative;
  opacity: 0.6;
}

@media (max-width: 450px) {
  svg {
    font-size: 2rem;
    margin: 1rem 0.5rem 1rem 0.5rem;
  }
}

@media (max-width: 320px) {
  svg {
    font-size: 2rem;
    margin: 1rem 0.2rem 1rem 0.2rem;
  }
}
</style>
