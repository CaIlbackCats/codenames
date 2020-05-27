<template>
    <div class="text-left">
        <div class="chat-div col-sm-12">
            <div class="message-window">
                <div v-for="chatMessage in chatMessages" :key="chatMessage.id">
                    <font-awesome-icon class="ml-2" v-if="chatMessage.name === currentPlayerName" icon="user-secret"/>
                    <label class="mx-2">{{chatMessage.name}}: {{chatMessage.message}}</label>
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
                              type="submit"
                              @click="sendChatMessage">Send
                    </b-button>
                </b-input-group-append>
            </b-input-group>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import {MessageModel} from "@/models/messageModel";
    import {PlayerModel} from "@/models/playerModel";


    @Component
    export default class LobbyChat extends Vue {

        private chatMessageToSend = "";

        mounted() {
            // TODO: can we get types for action payloads? use action creators?
            this.$store.dispatch("chatModule/subscribeToChat", this.lobbyId);
        }

        public sendChatMessage(): void {
            const msgModel: MessageModel = {
                name: this.currentPlayerName,
                message: this.chatMessageToSend,
                lobbyName: this.lobbyId,
            }
            this.chatMessageToSend = "";
            this.$store.dispatch("chatModule/sendChatMessage", msgModel)
        }

        get chatMessages() {
            return this.$store.getters["chatModule/messages"];
        }

        get currentPlayerName(): string {
            return this.$store.getters["currentPlayerName"];
        }

        get lobbyId(): string {
            return this.$store.getters["lobbyId"];
        }

    }
</script>

<style scoped>
    input {
        opacity: 0.6;
    }

    input:focus {
        opacity: 1;
        outline: none;
        box-shadow: none;
        border: none;
    }

    label {
        color: rgb(135, 25, 75);
    }

    .chat-div {
        height: 60vh;
        position: relative;
    }

    .message-window-background-div {
        height: 100%;
        width: 100%;
        background-color: white;
        position: relative;
        opacity: 0.6;
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

    svg {
        color: rgb(135, 25, 75);
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