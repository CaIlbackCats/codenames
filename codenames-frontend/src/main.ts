import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store"

Vue.config.productionTip = false;

const BASE_URL = "http:localhost:8080/api/chat";


new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");
