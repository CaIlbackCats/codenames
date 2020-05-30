<template>
    <div class="main-div col-sm-12 container">
        <div class="float-left mt-5 spy-num">
            404
        </div>
        <div class="float-left mt-5 spy">
            The page you requested cannot be found
        </div>
        <b-button squared
                  @click="goToMainPage"
                  @mouseover="isOverButton = true"
                  @mouseleave="isOverButton = false">
            Go to main page
        </b-button>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import router from '@/router'

    @Component({
        components: {}
    })
    export default class NotFound extends Vue {
        private isOverButton = false;


        public goToMainPage = () => {
            router.replace('/')
        }
    }
</script>

<style scoped>

    @font-face {
        font-family: 'SkyFall Done';
        src: url('SkyFallDone.woff2') format('woff2'),
        url('SkyFallDone.woff') format('woff');
        font-weight: normal;
        font-style: normal;
    }

    .container {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .main-div {
        background-image: url("../assets/background.svg");
        background-repeat: no-repeat;
        background-size: cover;
        background-position: top;
        min-height: 100vh;
        min-width: 100vw;
    }

    .spy-num {
        font-size: 80px;
        font-family: "SkyFall Done";
        color: white;
    }

    .spy {
        font-size: 30px;
        font-family: "SkyFall Done";
        color: white;
    }

    button {
        width: 150px;
        margin-top: 5vh;
        background-color: rgb(135, 25, 75);
        border: 0 solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, 0);
        outline: rgba(250, 230, 15, .5) solid 1px;
        outline-offset: 0px;
        text-shadow: none;
        transition: all 1250ms cubic-bezier(0.19, 1, 0.22, 1);
    }

    button:hover {
        width: 150px;
        margin-top: 5vh;
        background-color: rgb(135, 25, 75);
        border: 0px solid;
        box-shadow: inset 0 0 20px rgba(250, 230, 15, .5), 0 0 20px rgba(250, 230, 15, .2);
        outline-color: rgba(250, 230, 15, 0);
        outline-offset: 15px;
        text-shadow: 1px 1px 2px #427388;
    }

</style>