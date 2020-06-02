<template>
    <div>
        <div class="codenames-header">
            <b-img :src="logoUrl"></b-img>
        </div>
        <div class="col-sm-12 col-md-8 col-lg-6 col-xl-4 offset-md-2 offset-lg-3 offset-xl-4 mt-lg-5">
            <b-input-group size="md">
                <b-form-input id="current-player"
                              required
                              type="text"
                              maxlength="10"
                              :placeholder="$t('create-player.placeholder')"
                              v-on:keyup.enter="createPlayer"
                              v-model="currentPlayerName">
                </b-form-input>
                <b-input-group-append>
                    <b-button squared
                              type="submit"
                              :disabled="!isNameValid"
                              @click="createPlayer">Ok
                    </b-button>
                </b-input-group-append>
            </b-input-group>
            <p v-if="isNameError">
                {{$t("create-player.valid-name")}}
            </p>
        </div>
    </div>

</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {PlayerCreationModel} from "@/models/player/playerCreationModel";

    @Component
    export default class CreatePlayer extends Vue {
        private logoUrl = require("../assets/semanedoc.png");

        private currentPlayerName = "";
        private isNameValid = false;

        @Watch("currentPlayerName")
        private validateName() {
            this.isNameValid = !!this.currentPlayerName.match(RegExp("^[a-zA-Z0-9_]*$")) && this.currentPlayerName !== "";
        }

        public createPlayer(): void {
            const newPlayer: PlayerCreationModel = {
                lobbyName: this.$route.params.lobbyId,
                name: this.currentPlayerName,
            }
            this.$store.dispatch("sendPlayerCreation", newPlayer);
        }

        get isNameError(): boolean {
            return this.$store.getters["isNameError"];
        }


    }
</script>

<style scoped>

    p {
        color: white;
        font-weight: bold;
    }

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
        outline-color: rgba(135, 25, 75, .5);
        outline-offset: 0px;
        text-shadow: none;
        transition: all 1250ms cubic-bezier(0.19, 1, 0.22, 1);
    }

    button:enabled:hover {
        background-color: rgb(135, 25, 75);
        border: 0px solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, .5), 0 0 20px rgba(250, 230, 15, .2);
        outline-color: rgba(250, 230, 15, 0);
        outline-offset: 15px;
        text-shadow: 1px 1px 2px #427388;
    }

</style>