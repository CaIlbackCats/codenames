<template>
    <div>
        <input class="input" type="text" readonly :value="path"/>
        <button type="button" @click="copyPath">Szoba cím másolása</button>
        <lobby-chat></lobby-chat>
        <LobbyOption :lobby-name="path"></LobbyOption>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import LobbyChat from "@/components/LobbyChat.vue";
    import LobbyOption from "@/components/LobbyOption.vue";

    @Component({
        components: {LobbyOption, LobbyChat}
    })
    export default class Lobby extends Vue {

        private path = "http://localhost:4200";

        constructor() {
            super();
        };

        mounted() {
            // if (this.$store.lobby !== undefined) {
            //     dispatch('joinLobby')
            // }
            this.path += this.$route.path;
        };

        public copyPath(): void {
            this.$copyText(this.path);
        }

    }
</script>

<style scoped>
    .input {
        width: 500px
    }
</style>