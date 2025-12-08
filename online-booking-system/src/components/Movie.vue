<template>
  <div>
    <!-- 搜索框和日期选择器 -->
    <div class="centered">
      <el-form :inline="true" class="search-form">
        <el-form-item label="电影名">
          <el-input v-model="info.keyword" placeholder="请输入电影名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData()">
            <el-icon>
              <Search />
            </el-icon>
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button type="primary" @click="resetUpdateForm(), dialogFormVisible = true">
              新建
              <el-icon>
                <CirclePlus />
              </el-icon>
            </el-button>
            <el-button type="success" @click="exportToExcel()">
              导出
              <el-icon>
                <DocumentCopy />
              </el-icon>
            </el-button>
          </el-button-group>

        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-center">
      <el-table :data="movieList.list" border table-layout="auto">
        <el-table-column prop="mid" label="电影ID"></el-table-column>
        <el-table-column label="电影名">
          <template #default="scope">
            {{ scope.row.mname }}
            <el-button type="primary" size="small" @click="openChart(scope.row.mname)" style="margin-left: 8px">
              查看票房
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="director" label="导演"></el-table-column>
        <el-table-column prop="releaseDate" label="发行日期">
          <template #default="scope">
            {{ formatDate(scope.row.releaseDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="synopsis" label="简介"></el-table-column>
        <el-table-column prop="poster" label="海报url"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="runtime" label="时长(min)"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button-group>
              <el-button type="primary" size="small" @click="updateClick(scope.row)">编辑</el-button>
              <el-popconfirm title="确定要删除此记录吗？" confirm-button-text="是" cancel-button-text="手滑了"
                @confirm="deleteRow(scope.row)">
                <template #reference>
                  <el-button type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="centered">
      <el-pagination background layout="prev, pager, next" :total="movieList.totalSize" :page-size="movieList.pageSize"
        :current-page="movieList.pageNum" @current-change="handleCurrentChange" />
    </div>
  </div>

  <!-- 点击编辑后弹出的对话框 -->
  <el-dialog v-model="dialogFormVisible" title="当前编辑信息" width="400">
    <el-form ref="ruleFormRef" :model="updateForm" :rules="rules">
      <el-form-item label="电影ID" prop="mid">
        <el-input v-model="updateForm.mid" disabled></el-input>
      </el-form-item>
      <el-form-item label="电影名" prop="mname">
        <el-input v-model="updateForm.mname"></el-input>
      </el-form-item>
      <el-form-item label="导演" prop="director">
        <el-input v-model="updateForm.director"></el-input>
      </el-form-item>
      <el-form-item label="发行日期" prop="releaseDate">
        <el-date-picker v-model="updateForm.releaseDate" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="简介" prop="synopsis">
        <el-input type="textarea" v-model="updateForm.synopsis"></el-input>
      </el-form-item>
      <el-form-item label="海报URL" prop="poster">
        <el-input v-model="updateForm.poster"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input v-model="updateForm.type"></el-input>
      </el-form-item>
      <el-form-item label="时长(min)" prop="runtime">
        <el-input-number v-model="updateForm.runtime" :min="1" :max="300" label="时长"></el-input-number>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpdate(ruleFormRef)">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../axios/axios.js';
import * as XLSX from 'xlsx';

const dialogFormVisible = ref(false);
const ruleFormRef = ref(null); // 用于引用表单实例

const movieList = ref({
  totalSize: 0,
  totalPage: 0,
  pageSize: 10,
  list: [],
  pageNum: 1
});
const info = ref({
  pageNum: 1,
  pageSize: 10,
  date: null,
  keyword: null
});
//获取电影信息列表
async function fetchData() {
  try {
    const response = await request.post('/movie', info.value); // 获取后端响应的电影信息
    movieList.value = response.movieList;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

onMounted(() => {
  fetchData();
});
//日期格式化
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString();
}
//处理分页
function handleCurrentChange(page) {
  console.log(`切换到第 ${page} 页`);
  // 更新分页信息
  info.value.pageNum = page;
  // 重新加载数据
  fetchData();
}
const selectedRow = ref({
  mid: null,
});
function updateClick(row) {
  selectedRow.value.mid = row.mid;
  Object.assign(updateForm, row);
  dialogFormVisible.value = true;
}

// 编辑和新建操作对应的表单，交给后端处理插入和更新业务
const updateForm = reactive({
  mid: null,
  mname: null,
  director: null,
  releaseDate: null,
  synopsis: null,
  poster: null,
  type: null,
  runtime: null
});

// 用于重置表单数据的方法
function resetUpdateForm() {
  Object.assign(updateForm, {
    mid: null,
    mname: null,
    director: null,
    releaseDate: null,
    synopsis: null,
    poster: null,
    type: null,
    runtime: null
  });
}

// 表单验证规则
const rules = reactive({
  mname: [{ required: true, message: '电影名是必填项', trigger: 'blur' }],
  director: [{ required: true, message: '导演是必填项', trigger: 'blur' }],
  releaseDate: [{ required: true, message: '发行日期是必填项', trigger: 'blur' }],
  synopsis: [{ required: true, message: '简介是必填项', trigger: 'blur' }],
  poster: [{ required: true, message: '海报URL是必填项', trigger: 'blur' }],
  type: [{ required: true, message: '类型是必填项', trigger: 'blur' }],
  runtime: [{ required: true, message: '时长是必填项', trigger: 'blur' }]
});
//更新选中行的信息
async function submitUpdate(ruleFormRef) {
  if (!ruleFormRef) return;
  await ruleFormRef.validate(async (valid, fields) => {
    if (valid) {
      try {
        await request.put('/movie', updateForm);
        ElMessage.success('更新数据成功');
        fetchData();
        dialogFormVisible.value = false;
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    } else {
      console.log('There were validation errors.', fields);
    }
  });
}
//删除功能的实现
function deleteRow(row) {
  request.delete(`/movie/${row.mid}`
  ).then(() => {
    ElMessage.success('删除数据成功');
    fetchData();//刷新电影列表
  }).catch((error) => {
    console.error('Error deleting data:', error);
  });
}
// 添加一个新的方法来导出数据
async function exportToExcel() {
  try {
    movieList.value.list = await request.get('/movie');
  } catch (error) {
    console.error('Error fetching data:', error);
  }
  // 创建一个新的工作簿
  const wb = XLSX.utils.book_new();
  // 定义表头
  const header = ['电影ID', '电影名', '导演', '发行日期', '简介', '海报url', '类型', '时长(min)'];
  // 将表格数据转换为 worksheet，并且在数据前加上表头
  const ws = XLSX.utils.aoa_to_sheet([header, ...movieList.value.list.map(row => Object.values(row))]);
  // 添加 worksheet 到工作簿
  XLSX.utils.book_append_sheet(wb, ws, 'Movies');
  // 生成文件名
  const filename = 'movies.xlsx';
  // 将工作簿写入文件
  XLSX.writeFile(wb, filename);
  ElMessage.success('导出成功');
  fetchData();//刷新电影列表
}
</script>

<style scoped>
.centered {
  display: flex;
  justify-content: center;
  /* 水平居中 */
  align-items: flex-end;
  /* 垂直对齐方式为底部对齐 */
  position: relative;
  /* 设置相对定位 */
  bottom: 0;
  /* 距离底部的距离 */
  padding: 20px;
}

.table-center {
  margin: 0 auto;
  /* 使用自动边距使表格居中 */
}
</style>