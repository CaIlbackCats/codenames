import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";

import {MessageModel} from "@/models/messageModel";
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
        const path = `${config.wsChatSubscribePath}${lobbyId}`
        return websocket.subscribe(path, (message) => {
            if (message) this.context.commit("ADD_MESSAGE", message);
        });
    }

    @Action
    public sendChatMessage(messageModel: MessageModel) {
        return websocket.send(config.wsChatPublishPath, messageModel)
    }
}