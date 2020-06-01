import {Action, Module, Mutation, VuexModule} from "vuex-module-decorators";

import {MessageModel} from "@/models/chat/messageModel";
import * as websocket from '@/services/websocket'
import {Subscription} from "webstomp-client";

@Module
export default class ChatModule extends VuexModule {

    private chatMessages: Array<MessageModel> = [];


    @Mutation
    private ADD_MESSAGE(message: MessageModel): void {
        this.chatMessages.push(message);
    };

    @Action({rawError: true})
    public subscribeToChat(): Promise<Subscription> {
        const lobbyId: string = this.context.getters["lobbyId"];
        const path = "/chat/" + lobbyId;
        return websocket.subscribe(path, (message) => {
            if (message) {
                this.context.commit("ADD_MESSAGE", message);
            }
        });
    }

    @Action({rawError: true})
    public unsubscribeToChat(subscription: Subscription) {
        websocket.unsubscribe(subscription);
    }

    @Action
    public sendChatMessage(message: string): void {
        const lobbyId: string = this.context.getters["lobbyId"];
        const messageModel: MessageModel = {
            playerId: this.context.getters["currentPlayerId"],
            name: this.context.getters["currentPlayerName"],
            message: message,
            teamColor: "",
        }
        websocket.send("/chat/public/" + lobbyId, messageModel)
    }


    get messages(): Array<MessageModel> {
        return this.chatMessages;
    }
}