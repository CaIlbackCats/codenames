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
    import SockJS from "sockjs-client";
    import Stomp, {Client} from "webstomp-client";

    const BASE_URL = process.env.VUE_APP_BASE_URL
    const ENDPOINT_TO_SUBSCRIBE = process.env.VUE_APP_LISTEN_ENDPOINT;
    const ENDPOINT_TO_SEND = process.env.VUE_APP_SEND_ENDPOINT;

    @Component
    export default class LobbyChat extends Vue {

        @Prop()
        currentPlayer!: string;
        @Prop()
        currentLobby!: string;

        private stompClient!: Client;

        private chatMessageToSend = "";


        constructor() {
            super();
        };

        mounted() {
            this.connect();
        }

        public connect(): void {
            const socket = new SockJS(BASE_URL);
            this.stompClient = Stomp.over(socket);

            //kikapcsolja a loggolást
            this.stompClient.debug = () => {
                null
            };
            this.stompClient.connect({"name": this.currentPlayer}, frame => {
                this.subscribe();
            })

        }

        private subscribe() {
            this.stompClient.subscribe(ENDPOINT_TO_SUBSCRIBE + this.currentLobby, message => {
                if (message.body) {
                    this.$store.dispatch("addMessage", message);
                }
            });
        }

        private send(message: MessageModel) {
            this.stompClient.send(ENDPOINT_TO_SEND, JSON.stringify(message), {});
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
            return this.$store.getters.messages;
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