import Vue from "vue";
import Vuex from "vuex";
import chatModule from "@/store/modules/chatModule";

Vue.use(Vuex);


export default new Vuex.Store({
    state: {},
    mutations: {},
    actions: {},
    modules: {
        chatModule,
    }
});
