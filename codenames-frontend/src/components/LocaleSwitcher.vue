<template>
    <div class="locale-switcher">
<!--        <div style="font-size:180px">-->
<!--            <flag iso="gb" />-->
<!--        </div>-->
        <label>
            <select v-model="$i18n.locale" @change="onChange" class="select-box">
                <option selected value="en">English</option>
                <option value="hu">Hungarian</option>
            </select>
        </label>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import router from "@/router";

    @Component({})
    export default class LocaleSwitcher extends Vue{
        onChange(event){
            const selectedLanguage = event.target.value
            router.replace({name: 'Home', query: {lang: selectedLanguage}})
        }
    }
</script>

<style scoped>
    .locale-switcher {
        margin-top: 10px;
        position: absolute;
        top: 0;
        right: 0;
        left: 0;
    }

    .select-box {
        position: relative;
        display: block;
        width: 100%;
        margin: 0 auto;
        font-size: 18px;
        color: white;
        background-color: rgba(135, 25, 75, .5);
    }

</style>
