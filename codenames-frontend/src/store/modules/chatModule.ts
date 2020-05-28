import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import {MessageModel} from "@/models/chat/messageModel";
import * as websocket from '@/services/websocket'
import {config} from "@/config";

@Module({namespaced: true})
export default class ChatModule extends VuexModule {

    private chatMessages: Array<MessageModel> = [];

    get messages() {
        return this.chatMessages;
    }

    @Mutation
    private ADD_MESSAGE(message: MessageModel): void {
        this.chatMessages.push(message);
    };

    @Action({rawError: true})
    public subscribeToChat(lobbyId:string) {
        const path = `${config.CHAT_SUBSCRIPTION_PATH}${lobbyId}`
        return websocket.subscribe(path, (message) => {
            if (message) this.context.commit("ADD_MESSAGE", message);
        });
    }

    @Action
    public sendChatMessage(messageModel: MessageModel) {
        return websocket.send(config.CHAT_SEND_MSG_PATH, messageModel)
    }
}