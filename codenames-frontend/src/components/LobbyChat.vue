<template>
    <div class="col-sm-12 col-md-6">
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
    import {Component, Vue} from "vue-property-decorator";
    import {MessageModel} from "@/models/messageModel";

    @Component
    export default class LobbyChat extends Vue {

        //  private chatMessages: Array<MessageModel> = this.$store.state.chatMessages;
        private chatMessages: Array<MessageModel> = [];
        private chatMessageToSend = "";


        constructor() {
            super();
            //   this.chatMessages! = this.$store.state.chatMessages;
            // this.chatMessages! = this.$store.state.chatMessages;
            this.chatMessages = this.$store.getters.messages
            this.connect();
        };

        public connect(): void {
            this.$store.dispatch("connect");
        }


        public sendChatMessage(): void {
            //todo remove randomId and hardcoded name
            const id: number = this.generateId();
            const msgModel: MessageModel = {
                id: id,
                name: "Pecske",
                message: this.chatMessageToSend,
            }
            this.$store.dispatch("sendMsg", msgModel);
        }


        private generateId(): number {
            return Math.random() * 1000;
        }

    }
</script>

<style scoped>

    .message-window {
        width: 25vh;
        height: 25vh;
        overflow: auto;
    }

</style>