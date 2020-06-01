<template>
<<<<<<< HEAD
    <div class="align-center">
        <b-button :title="isEnoughPlayersToPlay ? 'There must be at least 4 players to use this function': '' "
                  :disabled="!isEnoughPlayersToPlay" @click="randomizeRoles" block size="sm" squared>Random role
        </b-button>
        <b-button :title="isEnoughPlayersToPlay ? 'There must be at least 4 players to use this function': '' "
                  :disabled="!isEnoughPlayersToPlay" @click="randomizeSide" block size="sm" squared>Random side
        </b-button>
        <b-button :title="isGameReadyToStart ? '' : '' "
                  :disabled="!isGameReadyToStart" @click="createGame" block class="mt-3" size="md" squared>Start The
            Game!
        </b-button>
        <b-button @click="setGameLanguage('ENGLISH')">English</b-button>
        <b-button @click="setGameLanguage('HUNGARIAN')">Hungarian</b-button>
    </div>
=======
  <div class="align-center">
    <b-button
      :title="
        partySize < MIN_PARTY_SIZE
          ? 'There must be at least 4 players to use this function'
          : ''
      "
      :disabled="partySize < MIN_PARTY_SIZE"
      @click="randomizeRoles"
      block
      size="sm"
      squared
      >
      {{$t("lobby-option.role")}}
<!--      Random role-->
    </b-button>
    <b-button
      :title="
        partySize < MIN_PARTY_SIZE
          ? 'There must be at least 4 players to use this function'
          : ''
      "
      :disabled="partySize < MIN_PARTY_SIZE"
      @click="randomizeSide"
      block
      size="sm"
      squared
      >
      {{$t("lobby-option.side")}}
<!--      Random side-->
    </b-button>
    <b-button
      :title="isEveryoneReady ? '' : ''"
      :disabled="!isEveryoneReady"
      @click="createGame"
      block
      class="mt-3"
      size="md"
      squared
      >
      {{$t("lobby-option.start")}}

      <!--      Start The Game!-->
    </b-button>
    <b-button @click="setGameLanguage('ENGLISH')">English</b-button>
    <b-button @click="setGameLanguage('HUNGARIAN')">Hungarian</b-button>
  </div>
>>>>>>> set hungarian language option with i18n
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import router from "@/router";
import { LanguageModel } from "@/models/languageModel";

<<<<<<< HEAD
    import {Component, Vue, Watch} from "vue-property-decorator";
    import router from "@/router";
    import {LanguageModel} from "@/models/languageModel";

    @Component
    export default class LobbyOption extends Vue {

        public setGameLanguage(language: string): void {
            const gameLanguage: LanguageModel = {
                language: language
            }
            this.$store.dispatch("setGameLanguage", gameLanguage);
        }
=======
@Component
export default class LobbyOption extends Vue {
  private MIN_PARTY_SIZE = 4;

  public setGameLanguage(language: string): void {
    const gameLanguage: LanguageModel = {
      language: language
    };
    this.$store.dispatch("setGameLanguage", gameLanguage);
  }
>>>>>>> set hungarian language option with i18n

  constructor() {
    super();
  }

  public createGame(): void {
    this.$store.dispatch("createGame");
  }

  public randomizeRoles(): void {
    this.$store.dispatch("sendRandomizeRole");
  }

  public randomizeSide(): void {
    this.$store.dispatch("sendRandomizeSide");
  }

<<<<<<< HEAD
        get lobbyId(): string {
            return this.$store.getters["lobbyId"];
        }

        get partySize(): number {
            return this.$store.getters["partySize"];
        }

        get isGameReadyToStart(): boolean {
            return this.$store.getters["isGameReadyToStart"];
        }

        get isEnoughPlayersToPlay(): boolean {
            return this.$store.getters["isEnoughPlayersToPlay"];
        }
    }
=======
  get isEveryoneReady(): boolean {
    return this.$store.getters["isEveryoneReady"];
  }

  get lobbyId(): string {
    return this.$store.getters["lobbyId"];
  }

  get partySize(): number {
    return this.$store.getters["partySize"];
  }
}
>>>>>>> set hungarian language option with i18n
</script>

<style scoped>
small {
  color: rgb(135, 25, 75);
  opacity: 0.6;
}

button {
  background-color: rgb(135, 25, 75);
  border: 0 solid;
  box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
  outline: rgba(135, 25, 75, 0.5) solid 1px;
  outline-offset: 0px;
  text-shadow: none;
  transition: all 1250ms cubic-bezier(0.19, 1, 0.22, 1);
}

button:enabled:hover {
  background-color: rgb(135, 25, 75);
  border: 0px solid;
  box-shadow: inset 0 0 20px rgba(250, 230, 15, 0.5),
    0 0 20px rgba(250, 230, 15, 0.2);
  outline-color: rgba(250, 230, 15, 0);
  outline-offset: 15px;
  text-shadow: 1px 1px 2px #427388;
}
</style>
