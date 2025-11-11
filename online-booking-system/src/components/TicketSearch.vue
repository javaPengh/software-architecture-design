<template>
  <div>
    <!-- 搜索框和日期选择器 -->
    <div class="centered">
      <el-form :inline="true" class="search-form">
        <el-form-item label="电影名称">
          <el-input v-model="info.keyword" placeholder="请输入电影名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="放映日期">
          <el-date-picker v-model="info.date" type="date" placeholder="选择日期" :disabled-date="disabledDate"
            :shortcuts="shortcuts" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">
            <el-icon> <Search /> </el-icon>
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-center">
      <el-table :data="sDtoList.list" border table-layout="auto">
        <el-table-column prop="sid" label="放映场次ID" ></el-table-column>
        <el-table-column prop="mname" label="电影姓名" ></el-table-column>
        <el-table-column prop="hname" label="影厅名称" ></el-table-column>
        <el-table-column prop="showTime" label="放映开始时间" >
          <template #default="scope">
            {{ formatDate(scope.row.showTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="showTime" label="放映结束时间" >
          <template #default="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="票价" ></el-table-column>
        <el-table-column prop="remainingSeats" label="剩余座位数" ></el-table-column>
        <el-table-column label="操作" >
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleBuyClick(scope.row)">购票</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="centered">
      <el-pagination background layout="prev, pager, next" :total="sDtoList.totalSize" :page-size="sDtoList.pageSize"
        :current-page="sDtoList.pageNum" @current-change="handleCurrentChange" />
    </div>
  </div>

  <!-- 点击购票后弹出的对话框 -->
  <el-dialog v-model="dialogFormVisible" title="请选择你想要购买的座位" width="400">
    <el-form :model="form">
      <el-cascader v-model="selectedSeat" :options="seatOptions" :props="{ expandTrigger: 'hover' }"
        :change-on-select="true" :clearable="false" :before-filter="beforeFilter" @change="handleChange"></el-cascader>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBuyTicket()">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import request from '../axios/axios.js';
import { useUserStore } from '../store/userStore.js';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const dialogFormVisible = ref(false);

const sDtoList = ref({
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
//获取放映场次信息列表
async function fetchData() {
  try {
    const response = await request.post('/screening/dtoList', info.value); // 获取后端响应的票务信息
    sDtoList.value = response.sDtoList;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

onMounted(() => {
  fetchData();
});

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
  return time.getTime() < Date.now() - 3600 * 1000 * 24 || time.getTime() > Date.now() + 3600 * 1000 * 24 * 14;
};

function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleString();
}

function handleCurrentChange(page) {
  console.log(`切换到第 ${page} 页`);
  // 更新分页信息
  info.value.pageNum = page;
  // 重新加载数据
  fetchData();
}

// 添加 seats 数据结构来保存座位状态
const seats = ref([]);
const selectedSeat = ref([]); // 用于存储当前选中的座位 ID，这里改为数组
const seatCaul = ref({
  rowCapacity: 0, // 每一排的座位数量
  seatCount: 0 // 该场次的总座位数，1号在第一排，每排座位排满进入下一排
});

function handleBuyClick(row) {
  const { sid, price } = row;
  form.sid = sid;
  form.price = price; // 将当前票价和场次id赋值给form，以便后端中购票逻辑处理
  seatCaul.value.rowCapacity = row.rowCapacity;
  seatCaul.value.seatCount = row.seatCount;

  // 先初始化座位布局
  initSeatLayout();

  // 更新座位状态
  updateSeatStatus(row.sid).then(() => {
    // 然后显示对话框
    dialogFormVisible.value = true;
  });
}

// 获取已售出座位
async function fetchSoldSeats(sid) {
  try {
    return await request.get(`/ticket/order/${sid}`);
  } catch (error) {
    console.error('Error fetching sold seats:', error);
    return [];
  }
}

function initSeatLayout() {
  seats.value = [];
  for (let i = 1; i <= seatCaul.value.seatCount; i++) {
    // 初始状态下所有座位都是可选的
    seats.value.push({ id: i, sold: false });
  }
}

// 更新座位状态
async function updateSeatStatus(sid) {
  const soldSeats = await fetchSoldSeats(sid);
  console.log('soldSeats:', soldSeats);
  seats.value.forEach(seat => {
    seat.sold = soldSeats.includes(seat.id);
  });
}

// 生成座位级联选择器的选项
const seatOptions = computed(() => {
  const options = [];
  const totalRows = Math.ceil(seatCaul.value.seatCount / seatCaul.value.rowCapacity);

  for (let row = 1; row <= totalRows; row++) {
    const rowOptions = [];
    const start = (row - 1) * seatCaul.value.rowCapacity + 1;
    const end = Math.min(start + seatCaul.value.rowCapacity - 1, seatCaul.value.seatCount);

    for (let seat = start; seat <= end; seat++) {
      rowOptions.push({
        value: seat,
        label: `${seat}号`,
        disabled: seats.value[seat - 1]?.sold || false, // 添加安全检查
      });
    }

    options.push({
      value: row,
      label: `${row}排`,
      children: rowOptions,
    });
  }

  return options;
});

// 监听级联选择器的改变事件
const handleChange = (value, selectedData) => {
  console.log('Selected:', value, selectedData);
};

// 要提交的数据，交给后端处理购票业务
const form = reactive({
  seatNumber: null,
  sid: null,
  price: null,
  uid: userStore.uid // 当前登录用户 uid
});
//提交选择的座位  
async function handleBuyTicket() {
  try {
    form.seatNumber = selectedSeat.value[1]; // 确保表单中的座位号是最新的
    await request.post('/ticket/order', form);
    ElMessage.success('下单成功，快去支付吧！');
    fetchData();//刷新票务列表
    dialogFormVisible.value = false; // 关闭对话框
  } catch (error) {
    console.error('Error handleBuyTicket:', error);
  }
}

</script>

<style scoped>
.centered {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: flex-end; /* 垂直对齐方式为底部对齐 */
  position: relative; /* 设置相对定位 */
  bottom: 0; /* 距离底部的距离 */
  padding: 20px;
}

.table-center {
  margin: 0 auto;
  /* 使用自动边距使表格居中 */
}
</style>