<template>
    <div class="about">
        <LobbyOption></LobbyOption>
    </div>
</template>

<script lang="ts">
    import LobbyPath from "@/components/Lobby.vue";

    import {Component, Vue} from "vue-property-decorator";
    import LobbyOption from "@/components/LobbyOption.vue";

    @Component({
        components: {LobbyOption, LobbyPath}
    })
    export default class About extends Vue {
    }
</script>