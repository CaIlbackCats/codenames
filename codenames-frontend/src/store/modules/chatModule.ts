import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";

import {MessageModel} from "@/models/messageModel";
import * as websocket from '@/services/websocket'
import {config} from "@/config";

interface SubscribeActionPayload {
    roomName: string
}
interface SendActionPayload {
    message: MessageModel
}

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

    @Action
    public subscribeToChat(payload: SubscribeActionPayload) {
        const path = `${config.wsChatSubscribePath}${payload.roomName}`
        return websocket.subscribe(path, (message) => {
            if (message) this.context.commit("ADD_MESSAGE", message);
        });
    }

    @Action
    public sendChatMessage(payload: SendActionPayload) {
        return websocket.send(config.wsChatPublishPath, payload.message)
    }
}