<template>
    <div class="locale-switcher">
        <select v-model="$i18n.locale">
            <option value="en">English</option>
            <option value="hu">Hungarian</option>
        </select>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component({})
    export default class LocaleSwitcher extends Vue{
    }
</script>

<style scoped>

</style>