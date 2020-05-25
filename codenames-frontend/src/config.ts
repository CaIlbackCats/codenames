export const config = {
    baseUrl: process.env.VUE_APP_BASE_URL,
    wsChatSubscribePath: process.env.VUE_APP_LISTEN_ENDPOINT,
    wsChatPublishPath: process.env.VUE_APP_SEND_ENDPOINT,
    wsLobbyPath: process.env.VUE_APP_OPTIONS,
    wsFetchPlayerPath: process.env.VUE_APP_PLAYER_FETCH,
    HIDE_PLAYER: "/player/hidePlayer",
    CREATE_GAME: "/game/createGame",
    LOBBY: "/lobby/",
    GAME_STATE_UPDATE:"/game/processVote",

}