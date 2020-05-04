import Vue from "vue";
import Vuex from "vuex";
import {MessageModel} from "@/models/messageModel";
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

Vue.use(Vuex);

const BASE_URL = "http://localhost:8080/api";
const socket = new SockJS(BASE_URL);
const stompClient = Stomp.over(socket);

export default new Vuex.Store({
    state: {
        chatMessages: Array<MessageModel>(),
    },
    mutations: {
        addItem(state, message: MessageModel): void {
            state.chatMessages.push(message);
            console.log("--------------" + message.message);
        }

    },
    actions: {
        connect(context): void {
            stompClient.connect({}, function (frame) {
                stompClient.subscribe("/topic/chat", message => {
                    if (message.body) {
                        const messageResult: MessageModel = JSON.parse(message.body);
                        context.commit('addItem', messageResult);
                    }
                });
            });
        },
        sendMsg(context, chatMessage: MessageModel): void {
            stompClient.send("/chat/public", JSON.stringify(chatMessage), {});
        }

    },
    modules: {}
});
