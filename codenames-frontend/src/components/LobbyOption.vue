<template>
    <div>
        <button @click="randomizeRoles">Véletlenszerű szerep kiosztás</button>
        <button @click="randomizeSide">Véletlenszerű csapat kiosztás</button>

    </div>

</template>

<script lang="ts">

    import {Component, Prop, Vue} from "vue-property-decorator";
    import {PlayerModel} from "@/models/playerModel";
    import {Client} from "webstomp-client";
    import {RoomModel} from "@/models/roomModel";

    @Component
    export default class LobbyOption extends Vue {

        @Prop()
        private readonly lobbyName!: string;

        @Prop()
        private stomp!: Client;

        constructor() {
            super();
        }


        public randomizeRoles(): void {
            this.stomp.send(process.env.VUE_APP_OPTIONS_ROLE_CHANGE, this.lobbyName);
        }

        public randomizeSide(): void {
            this.stomp.send(process.env.VUE_APP_OPTIONS_SIDE_CHANGE, this.lobbyName);
        }
    }
</script>

<style scoped>

</style>