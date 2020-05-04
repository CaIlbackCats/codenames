<template>
    <div>
        <div>
            <input id="chat-message" type="text" v-model="chatMessageToSend">
            <button @click="sendChatMessage">Küldés</button>
        </div>
        <div v-for="chatMessage in chatMessages" :key="chatMessage.name">
            {{chatMessage.name}}:{{chatMessage.message}}
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {MessageModel} from "@/models/messageModel";

    @Component
    export default class LobbyChat extends Vue {

        //  private chatMessages: Array<MessageModel> = this.$store.state.chatMessages;
        chatMessages = [];
        private chatMessageToSend = "";

        constructor() {
            super();
            this.chatMessages! = this.$store.state.chatMessages;
            this.connect();
        };

        public connect(): void {
            this.$store.dispatch("connect");
        }


        public sendChatMessage(): void {
            const msgModel: MessageModel = {
                name: "Pecske",
                message: this.chatMessageToSend,
            }
            this.$store.dispatch("sendMsg", msgModel);

        }

    }
</script>

<style scoped>

</style>