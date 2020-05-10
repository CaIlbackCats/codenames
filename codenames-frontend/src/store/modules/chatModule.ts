import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";
import {MessageModel} from "@/models/messageModel";
import Stomp, {Client, Message} from "webstomp-client";
import {RoomModel} from "@/models/roomModel";

@Module({namespaced: true})
export default class ChatModule extends VuexModule {

    private chatMessages: Array<MessageModel> = [];


    @Mutation
    private ADD_MESSAGE(message: Message): void {
        const messageResult: MessageModel = JSON.parse(message.body);
        this.chatMessages.push(messageResult);
    };

    @Action
    public subscribeToChat(room: RoomModel) {
        const client = room.stompClient;
        client.subscribe(process.env.VUE_APP_LISTEN_ENDPOINT + room.name, message => {
            if (message.body) {
                this.context.commit("ADD_MESSAGE", message);
            }
        });
    }

    get messages() {
        return this.chatMessages;
    }
}