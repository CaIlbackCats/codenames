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

    @Component
    export default class LobbyChat extends Vue {

        @Prop()
        currentPlayer!: string;
        @Prop()
        currentLobby!: string;

        //   private chatMessages: Array<MessageModel> = [];
        private chatMessageToSend = "";


        constructor() {
            super();
            //  this.chatMessages = this.$store.getters.messages
        };

        mounted(){
            this.connect();
        }

        public connect(): void {
            console.log(this.currentLobby)
            this.$store.dispatch("connect", this.currentLobby);
        }


        public sendChatMessage(): void {
            //todo remove randomId and hardcoded name
            const id: number = this.generateId();
            const msgModel: MessageModel = {
                id: id,
                name: this.currentPlayer,
                message: this.chatMessageToSend,
                lobbyName: this.currentLobby,
            }
            this.$store.dispatch("sendMsg", msgModel);
        }

        private generateId(): number {
            return Math.random() * 1000;
        }

        get chatMessages(): Array<MessageModel> {
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