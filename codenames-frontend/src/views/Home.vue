<template>
    <div class="home">
        <img alt="Vue logo" src="../assets/logo.png"/>
        <LobbyChat></LobbyChat>
    </div>
</template>

<script>
    // @ is an alias to /src
    import LobbyChat from "@/components/LobbyChat";

    export default {
        name: "Home",
        components: {
            LobbyChat,
        }
    };
</script>
