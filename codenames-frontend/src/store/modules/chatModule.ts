import { Action, Module, Mutation, VuexModule } from "vuex-module-decorators";

<<<<<<< HEAD
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
=======
import { MessageModel } from "@/models/chat/messageModel";
import * as websocket from "@/services/websocket";
import { config } from "@/config";
import { Subscription } from "webstomp-client";

@Module({ namespaced: true })
export default class ChatModule extends VuexModule {
  private chatMessages: Array<MessageModel> = [];

  get messages() {
    return this.chatMessages;
  }

  @Mutation
  private ADD_MESSAGE(message: MessageModel): void {
    this.chatMessages.push(message);
  }

  @Action({ rawError: true })
  public subscribeToChat(lobbyId: string): Promise<Subscription> {
    const path = `${config.CHAT_SUBSCRIPTION_PATH}${lobbyId}`;
    return websocket.subscribe(
      path,
      message => {
        if (message) this.context.commit("ADD_MESSAGE", message);
      },
      { name: "chat" }
    );
  }

  @Action({ rawError: true })
  public unsubscribeToChat(subsciption: Subscription) {
    websocket.unsubscribe(subsciption);
  }

  @Action
  public sendChatMessage(messageModel: MessageModel) {
    return websocket.send(config.CHAT_SEND_MSG_PATH, messageModel);
  }
}
>>>>>>> set hungarian language option with i18n
