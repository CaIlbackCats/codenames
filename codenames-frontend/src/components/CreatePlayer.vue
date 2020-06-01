<template>
<<<<<<< HEAD
    <div>
        <div class="codenames-header">
            <b-img :src="logoUrl"></b-img>
        </div>
        <div class="col-sm-12 col-md-8 col-lg-6 col-xl-4 offset-md-2 offset-lg-3 offset-xl-4 mt-lg-5">
            <b-input-group size="md">
                <b-form-input id="current-player"
                              type="text"
                              maxlength="10"
                              placeholder="Enter your name"
                              v-on:keyup.enter="createPlayer"
                              v-model="currentPlayerName">
                </b-form-input>
                <b-input-group-append>
                    <b-button squared
                              type="submit"
                              @click="createPlayer">Ok
                    </b-button>
                </b-input-group-append>
            </b-input-group>
            <span v-if="isNameError">Name already exist</span>
        </div>
=======
  <div>
    <div class="codenames-header">
      <b-img :src="logoUrl"></b-img>
>>>>>>> set hungarian language option with i18n
    </div>
    <div
      class="col-sm-12 col-md-8 col-lg-6 col-xl-4 offset-md-2 offset-lg-3 offset-xl-4 mt-lg-5"
    >
      <b-input-group size="md">
        <b-form-input
          id="current-player"
          type="text"
          maxlength="10"
          :placeholder="$t('create-player.placeholder')"
          v-on:keyup.enter="createPlayer"
          v-model="currentPlayerName"
        >
        </b-form-input>
        <b-input-group-append>
          <b-button squared type="submit" @click="createPlayer">Ok </b-button>
        </b-input-group-append>
      </b-input-group>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { PlayerCreationModel } from "@/models/player/playerCreationModel";

@Component
export default class CreatePlayer extends Vue {
  private logoUrl = require("../assets/semanedoc.png");

<<<<<<< HEAD
        get isNameError(): boolean {
            return this.$store.getters["isNameError"];
        }

=======
  private currentPlayerName = "";
>>>>>>> set hungarian language option with i18n

  public createPlayer(): void {
    const newPlayer: PlayerCreationModel = {
      lobbyName: this.$route.params.lobbyId,
      name: this.currentPlayerName
    };
    this.$store.dispatch("sendPlayerCreation", newPlayer);
  }
}
</script>

<style scoped>
input {
  text-align: center;
  opacity: 0.6;
  outline: none;
  box-shadow: none;
  border: none;
}

input:focus {
  opacity: 1;
  outline: none;
  box-shadow: none;
  border: none;
}

img {
  margin-top: 60px;
  max-width: 75%;
}

button {
  background-color: rgb(135, 25, 75);
  border: 0 solid;
  box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
  outline: 1px solid;
  outline-color: rgba(135, 25, 75, 0.5);
  outline-offset: 0px;
  text-shadow: none;
  transition: all 1250ms cubic-bezier(0.19, 1, 0.22, 1);
}

button:hover {
  background-color: rgb(135, 25, 75);
  border: 0px solid;
  box-shadow: inset 0 0 20px rgba(250, 230, 15, 0.5),
    0 0 20px rgba(250, 230, 15, 0.2);
  outline-color: rgba(250, 230, 15, 0);
  outline-offset: 15px;
  text-shadow: 1px 1px 2px #427388;
}
</style>
