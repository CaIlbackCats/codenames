<template>
    <div>
        <b-button squared @click="randomizeRoles">Random role</b-button>
        <b-button squared @click="randomizeSide">Random side</b-button>
        <button :disabled="!getEveryOneRdy">Start The Game!</button>
    </div>

</template>

<script lang="ts">

    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
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

        get getEveryOneRdy(): boolean {
            return this.$store.getters["getEveryOneRdy"];
        }
    }
</script>

<style scoped>

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

    button:hover {
        background-color: rgb(135, 25, 75);
        border: 0px solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, .5), 0 0 20px rgba(250, 230, 15, .2);
        outline-color: rgba(250, 230, 15, 0);
        outline-offset: 15px;
        text-shadow: 1px 1px 2px #427388;
    }
</style>