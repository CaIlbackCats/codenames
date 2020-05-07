<template>
    <div>
        <input type="text" readonly :value="path">
        <button type="button" @click="copyPath">Szoba cím másolása</button>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class LobbyPath extends Vue {

        private path = "http://localhost:4200";

        constructor() {
            super();
        };

        private createUUID() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        }

        mounted() {
            // randomPath = uuid
            this.path += this.$route.path;
        };

        public copyPath(): void {
            this.$copyText(this.path);
        }

    }
</script>

<style scoped>

</style>