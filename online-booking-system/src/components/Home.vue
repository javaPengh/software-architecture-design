<template>
  <section class="home">
    <h2>欢迎来到网上订票系统</h2>
    <p>请从左侧菜单选择您需要的服务。</p>
    <el-row :gutter="20" class="featured-services">
      <el-col :span="12" v-for="(service, index) in filteredServices" :key="index">
        <el-card shadow="hover" class="service-card">
          <router-link :to="service.route">
            <img :src="service.image" alt="Service Image" class="service-image" />
            <div class="card-body">
              <h3>{{ service.title }}</h3>
              <p>{{ service.description }}</p>
            </div>
          </router-link>
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>

<script setup>
import { computed } from 'vue';
import { useUserStore } from '../store/userStore.js';
import { ElCard, ElRow, ElCol } from 'element-plus'; // 引入 Element Plus 组件

const userStore = useUserStore();

// 动态生成服务列表
const featuredServices = computed(() => {
  if (userStore.isAdmin) {
    return [
      {
        title: '数据管理',
        description: '可以管理表中数据。',
        route: '/screening',
        image: '/assets/images/ticket_management.jpg',
      },
    ];
  } else {
    return [
      {
        title: '购票查询',
        description: '查询可用的电影票信息。',
        route: '/ticket-search',
        image: '/assets/images/ticket_query.jpg',
      },
      {
        title: '我的订单',
        description: '查看和管理您的订单记录。',
        route: '/my-orders',
        image: '/assets/images/my_orders.jpg',
      },
    ];
  }
});

// 过滤服务列表
const filteredServices = computed(() => {
  return featuredServices.value.filter(service => {
    if (userStore.isAdmin) {
      return service.route !== '/my-orders' && service.route !== '/ticket-search';
    } else {
      return service.route !== '/ticket-management';
    }
  });
});
</script>

<style scoped>
.home {
  padding: 2rem;
  text-align: center;
}

.featured-services {
  margin-top: 2rem;
}

.service-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}

.service-image {
  max-height: 150px; /* 调整图片最大高度 */
  width: 100%;
  object-fit: cover;
  transition: transform 0.3s ease-in-out;
}

.service-image:hover {
  transform: scale(1.1);
}

.card-body {
  padding: 1rem;
  text-align: center; /* 文本居中对齐 */
}

.card-body h3 {
  margin-bottom: 0.5rem;
}

.card-body p {
  margin-top: 0;
}
</style>