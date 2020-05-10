<template>
    <div class="col-sm-12 col-md-6 text-left">
        <input id="chat-message" type="text" v-model="chatMessageToSend">
        <button @click="sendChatMessage">Küldés</button>
        <div class="message-window">
            <div v-for="chatMessage in chatMessages" :key="chatMessage.id">
                {{chatMessage.name}}:{{chatMessage.message}}
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import {MessageModel} from "@/models/messageModel";
    import {Client} from "webstomp-client";
    import {RoomModel} from "@/models/roomModel";


    @Component
    export default class LobbyChat extends Vue {

        @Prop()
        currentPlayer!: string;
        @Prop()
        currentLobby!: string;
        @Prop()
        private stompClient!: Client;

        private chatMessageToSend = "";


        constructor() {
            super();
        };

        mounted() {
            const room: RoomModel = {
                name: this.currentLobby,
                stompClient: this.stompClient,
            }
            this.$store.dispatch("chatModule/subscribeToChat", room);
        }

        private send(message: MessageModel) {
            this.stompClient.send(process.env.VUE_APP_SEND_ENDPOINT, JSON.stringify(message), {});
        }


        public sendChatMessage(): void {
            const msgModel: MessageModel = {
                name: this.currentPlayer,
                message: this.chatMessageToSend,
                lobbyName: this.currentLobby,
            }
            this.send(msgModel);
        }

        get chatMessages() {
            return this.$store.getters["chatModule/messages"];
        }
    }
</script>

<style scoped>

    .message-window {
        width: 25vW;
        height: 25vh;
        overflow-y: scroll;
    }

</style>