<template>
    <div class="main-div col-sm-12">
        <div class="codenames-header">
            <b-img :src="logoUrl"></b-img>
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
    @Component({
    })
    export default class WelcomeHeader extends Vue {
        private logoUrl = require("../assets/semanedoc.png");

        public createLobby() {
            this.$store.dispatch("createLobby");
        }
    }
</script>

<style scoped>
    .main-div {
        background-image: url("../assets/background.svg");
        background-repeat: no-repeat;
        background-size: 100%;
        width: 100vw;
        height: 100vh;
    }

    img {
        margin-top: 60px;
        max-width: 75%;
        margin-bottom: 15vh;
    }

    button {
        background-color: rgb(135, 25, 75);
        border: 0 solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
        outline: rgba(135, 25, 75, .5) solid 1px;
        outline-offset: 0px;
        text-shadow: none;
        transition: all 1250ms cubic-bezier(0.19, 1, 0.22, 1);
    }

    button:hover {
        background-color: rgb(135, 25, 75);
        border: 0px solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, .5), 0 0 20px rgba(250, 230, 15, .2);
        outline-color: rgba(250, 230, 15, 0);
        outline-offset: 15px;
        text-shadow: 1px 1px 2px #427388;
    }
</style>