<template>
    <div class="codenames-header">
        <b-img :src="logoUrl"></b-img>
    </div>
    <div class="col-sm-12 col-md-8 col-lg-6 col-xl-4 offset-md-2 offset-lg-3 offset-xl-4">
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
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {PlayerCreationModel} from "@/models/playerCreationModel";

    @Component
    export default class CreatePlayer extends Vue {
        private logoUrl = require("../assets/semanedoc.png");

        private currentPlayerName = "";

        public createPlayer(): void {
            const newPlayer: PlayerCreationModel = {
                lobbyName: this.$route.params.lobbyId,
                name: this.currentPlayerName,
            }
            this.$store.dispatch("sendPlayerCreation", newPlayer);
        }


    }
</script>

<style scoped>

</style>