<template>
  <el-scrollbar>
    <!-- 导航菜单 -->
    <el-menu :default-openeds="['1']">
      <!-- 管理员子菜单 -->
      <el-sub-menu index="1" v-if="userStore.isAdmin">
        <template #title>
          <el-icon>
            <Edit />
          </el-icon>数据管理
        </template>
        <el-menu-item index="1-1" @click="navigateTo('/movie')">电影</el-menu-item>
        <el-menu-item index="1-2" @click="navigateTo('/hall')">影厅</el-menu-item>
        <el-menu-item index="1-3" @click="navigateTo('/screening')">放映场次</el-menu-item>
      </el-sub-menu>
      <!-- 非管理员子菜单 -->
      <el-sub-menu index="1" class="center-content" v-else>
        <template #title>
          <el-icon>
            <Tickets />
          </el-icon>系统功能
        </template>
        <el-menu-item index="1-1" @click="navigateTo('/ticket-search')">
          <template #title>
            购票查询
          </template>
        </el-menu-item>
        <el-menu-item index="1-2" @click="navigateTo('/my-orders')">
          <template #title>
            我的订单
          </template>
        </el-menu-item>
        <el-menu-item index="1-3" @click="navigateTo('/movie-search')">
          <template #title>
            电影搜索
          </template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { useUserStore } from '../store/userStore.js';
import { Edit, Tickets } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
const userStore = useUserStore();
const router = useRouter();

// 定义一个导航函数
const navigateTo = (path) => {
  router.push(path);
};

</script>

<style scoped></style>