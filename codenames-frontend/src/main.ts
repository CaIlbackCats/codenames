import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store"
import 'bootstrap/dist/css/bootstrap.css';
import VueClipboard from "vue-clipboard2";
import VueJSModal from "vue-js-modal";
//import 'bootstrap-vue/dist/bootstrap-vue.css';

Vue.use(VueClipboard);
Vue.use(VueJSModal);
Vue.config.productionTip = false;


const BASE_URL = "http:localhost:8080/api/chat";


new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");
