<template>
    <div>
        <div>
            <input id="chat-message" type="text" v-model="chatMessageToSend">
            <button @click="sendChatMessage">Küldés</button>
        </div>
        <div v-for="chatMessage in chatMessages" :key="chatMessage.id">
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

        //todo remove count and hardcoded name
        private count = 1;

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
                id: this.count + 1,
                //todo replace hardcoded name
                name: "Pecske",
                message: this.chatMessageToSend,
            }
            this.$store.dispatch("sendMsg", msgModel);

        }

    }
</script>

<style scoped>

</style>