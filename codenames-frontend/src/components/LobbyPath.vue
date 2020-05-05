<template>
    <div>
        <input type="text" readonly :value="path">
        <button type="button" @click="copyPath">Szoba cím másolása</button>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import VueClipboard from "vue-clipboard2";

    @Component
    export default class LobbyPath extends Vue {

        private path = "http://localhost:4200";

        constructor() {
            super();
        };

        mounted() {
            this.path += this.$route.path;
        };

        public copyPath(): void {
            this.$copyText(this.path);
        }

    }
</script>

<style scoped>

</style>