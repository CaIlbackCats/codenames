import Vue from "vue";
import VueRouter, { RouteConfig } from "vue-router";
import axios from 'axios'
import Home from "../views/Home.vue";
import Lobby from "@/components/Lobby.vue";
import WelcomeHeader from "@/components/WelcomeHeader.vue";

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
    component: WelcomeHeader
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/About.vue")
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
