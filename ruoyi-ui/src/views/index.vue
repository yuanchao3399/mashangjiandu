<template>
  <div>
    <home-data v-if="$store.getters.roles != 'swzx'"></home-data>
    <ts-info v-else></ts-info>
  </div>
</template>

<script>
import homeData from "./components/homeData.vue";
import tsInfo from './system/info/index.vue'
export default {
  components: { homeData, tsInfo },
};
</script>

<style scoped lang="scss">
</style>

