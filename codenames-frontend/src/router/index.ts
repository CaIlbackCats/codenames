import Vue from "vue";
import VueRouter, {RouteConfig} from "vue-router";
import Home from "../views/Home.vue";
import Lobby from "@/components/Lobby.vue";
import Game from "@/views/Game.vue";
import Statistics from '@/views/Statistics.vue';
import NotFound from "@/components/NotFound.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: "/",
    name: "Home",
    component: Home
  },
  {
    path: "/lobby/:lobbyId",
    name: "Lobby",
    component: Lobby,
  },
  {
    path: "/lobby/:lobbyId/:gameId",
    name: "Game",
    component: Game
  },
  {
    path: "/lobby/:lobbyId/:gameId/statistics",
    name: "Statistics",
    component: Statistics
  },
  {
    path: "/notFound",
    name: "NotFound",
    component: NotFound
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
        import(/* webpackChunkName: "about" */ "../views/About.vue")
  },
  {
    path: '*',
    name: '404',
    component: NotFound
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

router.beforeEach((to, from, next) => {
  if (Object.keys(to.query).length === 0 && Object.keys(from.query).length > 0) {
    const toWithQuery = Object.assign({}, to, {query: from.query});
    next(toWithQuery);
  } else {
    next()
  }
})

export default router;
