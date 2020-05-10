import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";
import {MessageModel} from "@/models/messageModel";
import SockJS from "sockjs-client";
import Stomp, {Message} from "webstomp-client";

@Module
export default class ChatModule extends VuexModule {

    private chatMessages: Array<MessageModel> = [];


    @Mutation
    private ADD_MESSAGE(message: MessageModel): void {
        this.chatMessages.push(message);
    };

    @Action({commit: "ADD_MESSAGE", rawError: true})
    public addMessage(message: Message) {
        const messageResult: MessageModel = JSON.parse(message.body);
        return messageResult;
    };

    get messages() {
        return this.chatMessages;
    }
}