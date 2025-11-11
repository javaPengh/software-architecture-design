<template>
  <div class="layout_container">
    <el-container>
      <el-header>
        <Header></Header>
      </el-header>
      <el-container>
        <el-aside>
          <Aside></Aside>
        </el-aside>
        <el-main>
          <Main></Main>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import Aside from "./components/Aside.vue";
import Header from "./components/Header.vue";
import Main from "./components/Main.vue";
import { onMounted } from "vue";
import { useUserStore } from "./store/userStore";

const userStore = useUserStore();
onMounted(() => {
  if(userStore.token!=''){
    userStore.getUserInfo();
  }
});
</script>

<style scoped>
.layout_container{
  margin: 0;
}
.el-header {
  padding: 0 10px;
  /* 设置文字颜色 */
  color: var(--el-text-color-primary);
  /* 设置背景颜色 */
  background: var(--el-color-primary-light-8);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-aside {
  /* 设置文字颜色 */
  color: var(--el-text-color-primary);
  /* 设置背景颜色 */
  background: var(--el-color-primary-light-8);
  position: absolute;
  width: 150px;
  height: 100%;
}
.el-main{
  position: absolute;
  left: 150px;
  right: 0;
  top: 60px;
  bottom: 0;
  padding: 20px;
}
</style>