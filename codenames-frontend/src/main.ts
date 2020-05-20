import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store"
import 'bootstrap/dist/css/bootstrap.css';
import VueClipboard from "vue-clipboard2";
import VueJSModal from "vue-js-modal";
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue';
import {library} from '@fortawesome/fontawesome-svg-core'
import {faUserSecret, faUserMinus, faCheck, faTimes} from '@fortawesome/free-solid-svg-icons'
import {faRedhat, faBlackTie} from '@fortawesome/free-brands-svg-icons'
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'
import ToggleButton from "vue-js-toggle-button";

library.add(faUserSecret, faUserMinus, faCheck, faTimes, faBlackTie, faRedhat)


Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

Vue.use(ToggleButton);
Vue.use(VueClipboard);
Vue.use(VueJSModal);
Vue.config.productionTip = false;

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");
