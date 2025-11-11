<template>
  <div>
    <!-- 搜索框和日期选择器 -->
    <div class="centered">
      <el-form :inline="true" class="search-form">
        <el-form-item label="电影ID">
          <el-input v-model="info.id" placeholder="请输入放映场次对应电影ID" clearable></el-input>
        </el-form-item>
        <el-form-item label="放映开始时间">
          <el-date-picker v-model="info.date" type="date" placeholder="选择放映开始时间" :disabled-date="disabledDate"
                          :shortcuts="shortcuts"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData()">
            <el-icon>
              <Search/>
            </el-icon>
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button type="primary" @click="insertClick()">
              新建
              <el-icon>
                <CirclePlus/>
              </el-icon>
            </el-button>
            <el-button type="success" @click="exportToExcel()">
              导出
              <el-icon>
                <DocumentCopy/>
              </el-icon>
            </el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-center">
      <el-table :data="screeningList.list" border table-layout="auto">
        <el-table-column prop="sid" label="放映场次ID"></el-table-column>
        <el-table-column prop="mid" label="电影ID"></el-table-column>
        <el-table-column prop="hid" label="影厅ID"></el-table-column>
        <el-table-column prop="showTime" label="放映开始时间">
          <template #default="scope">
            {{ formatDate(scope.row.showTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="放映结束时间">
          <template #default="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="票价"></el-table-column>
        <el-table-column prop="seatCount" label="总票数"></el-table-column>
        <el-table-column prop="remainingSeats" label="剩余票数"></el-table-column>
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
      <el-pagination background layout="prev, pager, next" :total="screeningList.totalSize"
                     :page-size="screeningList.pageSize" :current-page="screeningList.pageNum"
                     @current-change="handleCurrentChange"/>
    </div>
  </div>

  <!-- 点击编辑或新建后弹出的对话框 -->
  <el-dialog v-model="dialogFormVisible" title="当前编辑信息" width="400">
    <el-form ref="ruleFormRef" :model="updateForm" :rules="rules">
      <el-form-item label="放映场次ID">
        <el-input v-model="updateForm.sid" disabled></el-input>
      </el-form-item>
      <el-form-item label="电影ID" prop="mid">
        <el-input-number v-model="updateForm.mid" :min="1" label="电影ID"></el-input-number>
      </el-form-item>
      <el-form-item label="影厅ID" prop="hid">
        <el-input-number v-model="updateForm.hid" :min="1" :max="50" label="影厅ID"></el-input-number>
      </el-form-item>
      <el-form-item label="放映开始时间" prop="showTime">
        <el-date-picker v-model="updateForm.showTime" type="datetime" placeholder="选择时间"></el-date-picker>
      </el-form-item>
      <el-form-item label="票价" prop="price">
        <el-input-number v-model="updateForm.price" :min="20" :max="80" label="票价"></el-input-number>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submit(ruleFormRef, submitType)">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, onMounted, reactive} from 'vue';
import {ElMessage} from 'element-plus';
import request from '../axios/axios.js';
import * as XLSX from 'xlsx';
import {CirclePlus, DocumentCopy} from "@element-plus/icons-vue";

const dialogFormVisible = ref(false);
const submitType = ref(false);
const ruleFormRef = ref(null);

const screeningList = ref({
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
  id: null
});

//获取放映场次信息列表
async function fetchData() {
  try {
    const response = await request.post('/screening', info.value); // 获取后端响应的放映场次信息
    screeningList.value = response.screeningList;
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
  return date.toLocaleString();
}

const shortcuts = [
  {
    text: '今天',
    value: new Date(),
  },
  {
    text: '明天',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() + 1);
      return date;
    },
  },
  {
    text: '一周后',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() + 7);
      return date;
    },
  },
];

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 3600 * 1000 * 24 || time.getTime() > Date.now() + 3600 * 1000 * 24 * 30;
};

//处理分页
function handleCurrentChange(page) {
  console.log(`切换到第 ${page} 页`);
  // 更新分页信息
  info.value.pageNum = page;
  // 重新加载数据
  fetchData();
}

function insertClick() {
  resetUpdateForm();
  submitType.value = false;
  dialogFormVisible.value = true
}

function updateClick(row) {
  Object.assign(updateForm, row);
  submitType.value = true;
  dialogFormVisible.value = true;
}

// 编辑和新建操作对应的表单，交给后端处理插入和更新业务
const updateForm = reactive({
  sid: null,
  mid: null,
  hid: null,
  showTime: null,
  price: null,
});

// 定义验证规则
const rules = reactive({
  mid: [
    {required: true, message: '请输入电影ID', trigger: 'change'}
  ],
  hid: [
    {required: true, message: '请输入影厅ID', trigger: 'change'}
  ],
  showTime: [
    {required: true, message: '请选择放映开始时间', trigger: 'change'}
  ],
  price: [
    {required: true, message: '请输入票价', trigger: 'change'}
  ],
});

// 用于重置表单数据的方法
function resetUpdateForm() {
  Object.assign(updateForm, {
    sid: null,
    mid: null,
    hid: null,
    showTime: null,
    price: null,
  });
}

// 向后端提交新建或者更新操作的表单,根据submitType的类型来定义,提交类型为true表示更新，否则为插入
async function submit(ruleFormRef, submitType) {
  if (!ruleFormRef) return;
  await ruleFormRef.validate(async (valid, fields) => {
    if (valid) {
      try {
        if (submitType) {
          console.log('更新')
          console.log(updateForm)
          await request.put('/screening/update', updateForm);
        } else {
          console.log('插入')
          await request.put('/screening/insert', updateForm);
        }
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
  request.delete(`/screening/${row.sid}`
  ).then(() => {
    ElMessage.success('删除数据成功');
    fetchData();//刷新放映场次列表
  }).catch((error) => {
    console.error('Error deleting data:', error);
  });
}

// 添加一个新的方法来导出数据
async function exportToExcel() {
  try {
    screeningList.value.list = await request.get('/screening');
  } catch (error) {
    console.error('Error fetching data:', error);
  }
  // 创建一个新的工作簿
  const wb = XLSX.utils.book_new();
  // 定义表头
  const header = ['放映场次ID', '电影ID', '影厅ID', '放映开始时间', '放映结束时间', '票价', '总票数', '剩余票数'];
  // 将表格数据转换为 worksheet，并且在数据前加上表头
  const ws = XLSX.utils.aoa_to_sheet([header, ...screeningList.value.list.map(row => Object.values(row))]);
  // 添加 worksheet 到工作簿
  XLSX.utils.book_append_sheet(wb, ws, 'Screenings');
  // 生成文件名
  const filename = 'screenings.xlsx';
  // 将工作簿写入文件
  XLSX.writeFile(wb, filename);
  ElMessage.success('导出成功');
  fetchData();//刷新放映场次列表
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