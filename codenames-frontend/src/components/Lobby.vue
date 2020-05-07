<template>
    <div>
        <input class="input" type="text" readonly :value="path" />
        <button type="button" @click="copyPath">Szoba cím másolása</button>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
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
    .input{
        width: 500px
    }
</style>