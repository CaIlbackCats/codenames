<template>
    <div class="text-left">
        <div class="chat-div col-sm-12">
            <div class="message-window">
                <div v-for="chatMessage in chatMessages" :key="chatMessage.id">
                    {{chatMessage.name}}:{{chatMessage.message}}
                </div>
            </div>
            <div class="message-window-background-div"></div>
        </div>
        <div class="col-sm-12 my-3">
            <b-input-group size="sm">
                <b-form-input id="chat-message"
                              type="text"
                              v-on:keyup.enter="sendChatMessage"
                              v-model="chatMessageToSend"></b-form-input>
                <b-input-group-append>
                    <b-button squared
                              type="button"
                              @click="sendChatMessage">Send</b-button>
                </b-input-group-append>
            </b-input-group>
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

    .chat-div{
        height: 60vh;
        position: relative;
    }

    .message-window-background-div{
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.3;
    }

    .message-window {
        height: 100%;
        width: 100%;
        overflow-y: scroll;
        overflow-x: hidden;
        position: absolute;
        z-index: 1;
    }

    .message-window::-webkit-scrollbar {
        display: none;
    }
    .message-window {
        -ms-overflow-style: none;
    }

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