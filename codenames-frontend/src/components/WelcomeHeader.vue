<template>
    <div class="main-div col-sm-12">
        <div class="codenames-header">
            <b-img :src="logoUrl" ></b-img>
        </div>
        <div class="text-center">
            <b-button squared @click="createLobby" class="mr-5">
                Create private room
            </b-button>
            <b-button squared>
                Random room
            </b-button>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import router from "@/router";

    @Component
    export default class WelcomeHeader extends Vue {

        private logoUrl = require("../assets/codenames.png");

        public createLobby() {
            this.$store.dispatch("createLobby");
           // router.push({name: "Lobby", params: {lobbyId: lobbyModel.id}});
        }
    }
</script>

<style scoped>

    button {
        background-color: rgb(135, 25, 75);
        border: none;
    }

    img {
        max-width: 100%;
        margin-bottom: 15vh;
    }

    /*fix active grey */
    button:hover,button:active,button:focus {
        outline: none;
        box-shadow: none;
        background-color: rgb(180, 65, 75);
    }

</style>