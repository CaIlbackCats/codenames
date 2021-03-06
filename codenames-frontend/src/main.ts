import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store"
import 'bootstrap/dist/css/bootstrap.css';
import VueClipboard from "vue-clipboard2";
import VueJSModal from "vue-js-modal";
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue';
import {library} from '@fortawesome/fontawesome-svg-core'
import {faUserSecret, faUserMinus, faCheck, faTimes, faWineBottle, faCocktail, faBriefcase} from '@fortawesome/free-solid-svg-icons'
import {faRedhat, faBlackTie} from '@fortawesome/free-brands-svg-icons'
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'
import ToggleButton from "vue-js-toggle-button";
import i18n from './i18n';
import FlagIcon from 'vue-flag-icon'

library.add(faUserSecret, faUserMinus, faCheck, faTimes, faBlackTie, faRedhat, faWineBottle, faCocktail, faBriefcase)


Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)
Vue.use(FlagIcon);

Vue.use(ToggleButton);
Vue.use(VueClipboard);
Vue.use(VueJSModal);
Vue.config.productionTip = false;

new Vue({
    router,
    store,
    i18n,
    render: h => h(App)
}).$mount("#app");
