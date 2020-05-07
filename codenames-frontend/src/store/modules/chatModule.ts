import {Action, Module, Mutation, MutationAction, VuexModule} from "vuex-module-decorators";
import {MessageModel} from "@/models/messageModel";
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

//const BASE_URL = "http://localhost:8080/api";
const BASE_URL = process.env.VUE_APP_BASE_URL
const ENDPOINT_TO_SUBSCRIBE = process.env.VUE_APP_LISTEN_ENDPOINT;
const ENDPOINT_TO_SEND = process.env.VUE_APP_SEND_ENDPOINT;
const socket = new SockJS(BASE_URL);
const stompClient = Stomp.over(socket);

@Module
export default class ChatModule extends VuexModule {

    public chatMessages: Array<MessageModel> = [];

    @Mutation
    private addItem(message: MessageModel): void {
        this.chatMessages.push(message);
    };

    @Action
    public connect() {
        stompClient.connect({}, (frame) => {
            stompClient.subscribe(ENDPOINT_TO_SUBSCRIBE, message => {
                if (message.body) {
                    const messageResult: MessageModel = JSON.parse(message.body);
                    this.context.commit('addItem', messageResult)
                }
            });
        });
    };

    @Action
    public sendMsg(chatMessage: MessageModel): void {
        stompClient.send(ENDPOINT_TO_SEND, JSON.stringify(chatMessage), {});
    }

    get messages() {
        return this.chatMessages;
    }
}