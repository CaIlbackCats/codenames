<template>
    <div class="main-div">
        <div class="codenames-header">
            <h1>Codenames</h1>
        </div>
        <div class="col-sm-12">
            <button class="btn rounded-pill private-room-button" @click="createLobby">
                Create private room
            </button>
            <button class="btn rounded-pill random-room-button">
                Random room
            </button>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class WelcomeHeader extends Vue {

        public createLobby() {
            this.$store.dispatch("createLobby");
        }
    }
</script>

<style scoped>

</style>