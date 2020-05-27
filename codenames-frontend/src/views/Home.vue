<template>
    <div class="home">
        <welcome-header></welcome-header>
    </div>
</template>

<script lang="ts">
    // @ is an alias to /src
    import {Component, Vue} from "vue-property-decorator";
    import WelcomeHeader from "@/components/WelcomeHeader.vue";

    @Component({
        components: {WelcomeHeader}
    })
    export default class Home extends Vue {
    }
</script>
