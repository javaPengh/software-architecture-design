<template>
  <div>
    <!-- 搜索框-->
    <div class="centered">
      <el-form :inline="true" class="search-form">
        <el-form-item label="影厅名">
          <el-input v-model="info.keyword" placeholder="请输入影厅名" clearable></el-input>
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
      <el-table :data="hallList.list" border table-layout="auto">
        <el-table-column prop="hid" label="影厅ID"></el-table-column>
        <el-table-column prop="hname" label="影厅名"></el-table-column>
        <el-table-column prop="rowNum" label="影厅排数"></el-table-column>
        <el-table-column prop="rowCapacity" label="每排座位数"></el-table-column>
        <el-table-column prop="screenType" label="荧幕类型"></el-table-column>
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
      <el-pagination background layout="prev, pager, next" :total="hallList.totalSize" :page-size="hallList.pageSize"
        :current-page="hallList.pageNum" @current-change="handleCurrentChange" />
    </div>
  </div>

  <!-- 点击编辑后弹出的对话框 -->
<el-dialog v-model="dialogFormVisible" title="当前编辑信息" width="400">
  <el-form ref="ruleFormRef" :model="updateForm" :rules="rules">
    <el-form-item label="影厅ID" prop="hid">
      <el-input v-model="updateForm.hid" disabled></el-input>
    </el-form-item>
    <el-form-item label="影厅名" prop="hname">
      <el-input v-model="updateForm.hname"></el-input>
    </el-form-item>
    <el-form-item label="影厅排数" prop="rowNum">
      <el-input-number v-model="updateForm.rowNum" :min="5" :max="20" label="影厅排数"></el-input-number>
    </el-form-item>
    <el-form-item label="每排座位数" prop="rowCapacity">
      <el-input-number v-model="updateForm.rowCapacity" :min="10" :max="20" label="每排座位数"></el-input-number>
    </el-form-item>
    <el-form-item label="荧幕类型" prop="screenType">
      <el-input v-model="updateForm.screenType"></el-input>
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
import { ref, onMounted, reactive, watch} from 'vue';
import { ElMessage, ElForm} from 'element-plus';
import request from '../axios/axios.js';
import * as XLSX from 'xlsx';

const dialogFormVisible = ref(false);
const ruleFormRef = ref(null);

const hallList = ref({
  totalSize: 0,
  totalPage: 0,
  pageSize: 10,
  list: [],
  pageNum: 1
});
const info = ref({
  pageNum: 1,
  pageSize: 10,
  keyword: null
});
//获取影厅信息列表
async function fetchData() {
  try {
    const response = await request.post('/hall', info.value); // 获取后端响应的影厅信息
    hallList.value = response.hallList;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

onMounted(() => {
  fetchData();
});

//处理分页
function handleCurrentChange(page) {
  console.log(`切换到第 ${page} 页`);
  // 更新分页信息
  info.value.pageNum = page;
  // 重新加载数据
  fetchData();
}
const selectedRow = ref({
  hid: null,
});
function updateClick(row) {
  selectedRow.value.hid = row.hid;
  Object.assign(updateForm, row);
  dialogFormVisible.value = true;
}

// 编辑和新建操作对应的表单，交给后端处理插入和更新业务
const updateForm = reactive({
  hid: null,
  hname: null,
  rowNum: null,
  rowCapacity: null,
  screenType: null
});

// 用于重置表单数据的方法
function resetUpdateForm() {
  Object.assign(updateForm, {
    hid: null,
    hname: null,
    rowNum: null,
    rowCapacity: null,
    screenType: null
  });
}
// 定义验证规则
const rules = reactive({
  hname: [
    { required: true, message: '请输入影厅名', trigger: 'blur' }
  ],
  rowNum: [
    { required: true, message: '请输入影厅排数', trigger: 'change' }
  ],
  rowCapacity: [
    { required: true, message: '请输入每排座位数', trigger: 'change' }
  ],
  screenType: [
    { required: true, message: '请输入荧幕类型', trigger: 'blur' }
  ]
});

// 更新提交表单的方法
async function submitUpdate(ruleFormRef) {
  if (!ruleFormRef) return;
  await ruleFormRef.validate(async (valid, fields) => {
    if (valid) {
      try {
        await request.put('/hall', updateForm);
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
  request.delete(`/hall/${row.hid}`
  ).then(() => {
    ElMessage.success('删除数据成功');
    fetchData();//刷新影厅列表
  }).catch((error) => {
    console.error('Error deleting data:', error);
  });
}
// 添加一个新的方法来导出数据
async function exportToExcel() {
  try {
    hallList.value.list = await request.get('/hall');
  } catch (error) {
    console.error('Error fetching data:', error);
  }
  // 创建一个新的工作簿
  const wb = XLSX.utils.book_new();
  // 定义表头
  const header = ['影厅ID', '影厅名', '影厅排数', '每排座位数', '荧幕类型'];
  // 将表格数据转换为 worksheet，并且在数据前加上表头
  const ws = XLSX.utils.aoa_to_sheet([header, ...hallList.value.list.map(row => Object.values(row))]);
  // 添加 worksheet 到工作簿
  XLSX.utils.book_append_sheet(wb, ws, 'Halls');
  // 生成文件名
  const filename = 'halls.xlsx';
  // 将工作簿写入文件
  XLSX.writeFile(wb, filename);
  ElMessage.success('导出成功');
  fetchData();//刷新影厅列表
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