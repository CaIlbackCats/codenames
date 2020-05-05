<template>
    <div class="about">
    </div>
</template>
<script lang="ts">
    import LobbyPath from "@/components/LobbyPath.vue";

    import {Component, Vue} from "vue-property-decorator";

    @Component({
        components: {LobbyPath}
    })
    export default class About extends Vue {
    }
</script>