<template>
  <div>
    <h2>电影搜索</h2>
    <!-- 搜索框 -->
    <div class="search-container">
      <el-input 
        v-model="searchKeyword" 
        placeholder="请输入电影名、导演或类型进行搜索" 
        clearable
        @keyup.enter="performSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="performSearch" />
        </template>
      </el-input>
      
      <el-button type="primary" @click="syncMovies" :loading="syncLoading">
        同步电影数据到搜索引擎
      </el-button>
    </div>

    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="results-container">
      <el-card 
        v-for="movie in searchResults" 
        :key="movie.mid" 
        class="movie-card"
      >
        <template #header>
          <div class="card-header">
            <span>{{ movie.mname }}</span>
            <el-tag type="primary">{{ movie.type }}</el-tag>
          </div>
        </template>
        <div class="movie-info">
          <p><strong>导演:</strong> {{ movie.director }}</p>
          <p><strong>时长:</strong> {{ movie.runtime }} 分钟</p>
          <p><strong>简介:</strong> {{ movie.synopsis }}</p>
          <p><strong>上映日期:</strong> {{ formatDate(movie.releaseDate) }}</p>
        </div>
      </el-card>
    </div>
    
    <!-- 无结果提示 -->
    <div v-else-if="searchPerformed && searchResults.length === 0" class="no-results">
      <el-empty description="没有找到相关电影" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '../axios/axios.js'

const searchKeyword = ref('')
const searchResults = ref([])
const searchPerformed = ref(false)
const syncLoading = ref(false)

// 执行搜索
async function performSearch() {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  try {
    const response = await request.get(`/search/movie/keyword/${encodeURIComponent(searchKeyword.value)}`)
    // 1. 使用 ?. 防止 response 为 undefined 时报错
    // 2. 使用 || 或 ?? 确保 searchResults.value 至少是个空数组 []
    console.log('后端返回的原始数据:', response)
    searchResults.value = response ?? [];

    searchPerformed.value = true;
    ElMessage.success(`找到 ${searchResults.value.length} 部相关电影`);
  } catch (error) {
    console.error('搜索失败:', error);
    // 搜索失败时显式清空结果，防止页面显示旧数据
    searchResults.value = [];
    ElMessage.error('搜索失败，请稍后重试');
  }
}

// 同步电影数据到 Elasticsearch
async function syncMovies() {
  syncLoading.value = true
  try {
    const response = await request.post('/search/movie/sync')
    ElMessage.success(response.message || '电影数据同步成功')
    // 清空之前的搜索结果
    searchResults.value = []
    searchPerformed.value = false
  } catch (error) {
    console.error('同步失败:', error)
    ElMessage.error('同步失败，请稍后重试')
  } finally {
    syncLoading.value = false
  }
}

// 格式化日期
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString()
}
</script>

<style scoped>
.search-container {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  align-items: center;
}

.search-container .el-input {
  flex: 1;
}

.results-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.movie-card {
  transition: transform 0.3s ease;
}

.movie-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.movie-info p {
  margin: 8px 0;
}

.no-results {
  text-align: center;
  padding: 40px 0;
}
</style>