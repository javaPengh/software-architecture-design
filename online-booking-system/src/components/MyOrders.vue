<template>
  <div>
    <!-- 表格 -->
    <div class="table-center">
      <el-table :data="tDtoList.list" border table-layout="auto">
        <el-table-column prop="tid" label="订单号"></el-table-column>
        <el-table-column prop="mname" label="电影姓名"></el-table-column>
        <el-table-column prop="hname" label="影厅名称"></el-table-column>
        <el-table-column prop="seatNumber" label="座位号"></el-table-column>
        <el-table-column prop="showTime" label="放映开始时间">
          <template #default="scope">
            {{ formatDate(scope.row.showTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="showTime" label="放映结束时间">
          <template #default="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="purchaseTime" label="下单时间">
          <template #default="scope">
            {{ formatDate(scope.row.purchaseTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="票价"></el-table-column>
        <el-table-column prop="orderStatus" label="订单状态">
          <template #default="scope">
            <el-tag :type="scope.row.orderStatus === '已取消' ? 'danger' :
              scope.row.orderStatus === '待支付' ? 'warning' :
                'success'" disable-transitions>{{ scope.row.orderStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button-group>
              <el-button type="primary" size="small" :disabled="scope.row.orderStatus !== '待支付'"
                @click="pay(scope.row)">支付</el-button>
              <el-popconfirm title="确定要取消订单吗？" confirm-button-text="是" cancel-button-text="手滑了"
                @confirm="cancelOrder(scope.row)">
                <template #reference>
                  <el-button type="danger" size="small" :disabled="!['待支付'].includes(scope.row.orderStatus)">
                    取消订单
                  </el-button>
                </template>
              </el-popconfirm>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 分页 -->
    <div class="centered">
      <el-pagination background layout="prev, pager, next" :total="tDtoList.totalSize" :page-size="tDtoList.pageSize"
        :current-page="tDtoList.pageNum" @current-change="handleCurrentChange" />
    </div>
  </div>

</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../axios/axios.js';
import { useUserStore } from '../store/userStore.js';
import { ElMessage } from 'element-plus';
const userStore = useUserStore();
const tDtoList = ref({
  totalSize: 0,
  totalPage: 0,
  pageSize: 10,
  list: [],
  pageNum: 1
});

const info = ref({
  pageNum: 1,
  pageSize: 10,
  id: userStore.uid
});
//获取订单列表
async function fetchData() {
  try {
    const response = await request.post('/ticket/getOrders', info.value); // 获取后端响应的票务信息
    tDtoList.value = response.tDtoList;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

onMounted(() => {
  fetchData();
});

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

// 要提交的数据，交给后端处理退订业务
const tid = ref(null);

//支付
async function pay(row) {
  try {
    window.open(`http://localhost:8080/alipay/pay?subject=${row.mname}&traceNo=${row.tid}&totalAmount=${row.price}`,'_self')
  } catch (error) {
    console.error('Error pay:', error);
  }
}
// 取消订单
async function cancelOrder(row) {
  tid.value = row.tid;
  try {
    await request.put(`/ticket/order/${tid.value}`);
    ElMessage.success('退订成功');
    fetchData();//刷新订单列表
  } catch (error) {
    console.error('Error cancel order:', error);
  }
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