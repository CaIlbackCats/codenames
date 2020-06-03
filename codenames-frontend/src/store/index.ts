import Vue from "vue";
import Vuex from "vuex";
import chatModule from "@/store/modules/chatModule";
import playerModule from "@/store/modules/playerModule";
import lobbyModule from '@/store/modules/lobbyModule'
import gameModule from "@/store/modules/gameModule";
import kickModule from "@/store/modules/kickModule";

Vue.use(Vuex);


export default new Vuex.Store({
    state: {},
    mutations: {},
    actions: {},
    modules: {
        chatModule,
        gameModule,
        kickModule,
        lobbyModule,
        playerModule,
    }
});
