import Vue from "vue";
import Vuex from "vuex";
import chatModule from "@/store/modules/chatModule";
import playerModule from "@/store/modules/playerModule";
import lobbyModule from '@/store/modules/lobbyModule'

Vue.use(Vuex);


export default new Vuex.Store({
    state: {},
    mutations: {},
    actions: {},
    modules: {
        chatModule,
        playerModule,
        lobbyModule
    }
});
