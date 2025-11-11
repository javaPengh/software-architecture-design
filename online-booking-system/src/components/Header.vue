<template>
  <div class="logo">
    <el-icon>
      <ShoppingCart />
    </el-icon>
    电影院线上订票系统
  </div>
  <div class="actions">
    <template v-if="!userStore.isLoggedIn">
      <el-button type="primary" @click="handleLoginClick">
        <el-icon>
          <User />
        </el-icon>登录
      </el-button>
      <el-button type="primary" @click="handleRegisterClick">
        <el-icon>
          <Edit />
        </el-icon>注册
      </el-button>
    </template>

    <template v-else>
      <span class="welcome-message" v-if="userStore.isLoggedIn">你好，{{ userStore.username }}！</span>
      <el-dropdown trigger="hover">
        <span class="el-dropdown-link">
          账户管理<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handlePasswordChange">修改密码</el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>
    <el-dialog title="修改密码" v-model="dialogVisible" width="25%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" :inline="false" size="default"
               label-position="top">                                                                                                                         
        <el-form-item prop="oldPwd" label="原密码">
          <el-input v-model="form.oldPwd" type="password" show-password clearable></el-input>
        </el-form-item>
        <el-form-item prop="newPwd" label="新密码">
          <el-input v-model="form.newPwd" type="password" show-password clearable></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword" label="确认新密码">
          <el-input v-model="form.confirmPassword" type="password" show-password clearable></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="resetForm">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useUserStore } from '../store/userStore.js';
import { useRouter } from 'vue-router';
import { Edit, User } from '@element-plus/icons-vue';
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const router = useRouter();
const dialogVisible = ref(false);
const formRef = ref(null);
const form = reactive({
  uid: userStore.uid,
  oldPwd: '',
  newPwd: '',
  confirmPassword: ''
});

// 表单验证规则
const rules = reactive({
  oldPwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
  ],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },

    {
      validator: (rule, value, callback) => {
        if (value !== form.newPwd) {
          callback(new Error('两次输入的密码不一致，请重新输入'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    },
  ],
});

const handleLoginClick = () => {
  router.push('/login');
};

const handleRegisterClick = () => {
  router.push('/register');
};

const handlePasswordChange = () => {
  dialogVisible.value = true;
  resetForm();
};

const handleLogout = () => {
  userStore.logout();
  router.push('/');
};

const resetForm = () => {
  form.oldPwd = '';
  form.newPwd = '';
  form.confirmPassword = '';
  formRef.value?.resetFields();
};

const submitForm = async () => {
  const valid = await formRef.value?.validate();
  if (valid) {
    try {
      await userStore.changePassword(form.oldPwd, form.newPwd);
      ElMessage.success('修改成功');
      dialogVisible.value = false;
    } catch (error) {
      ElMessage.error('修改失败，请检查输入');
    }
  } else {
    ElMessage.error('请填写完整的信息。');
  }
};
</script>

<style scoped>
.logo {
  font-size: 24px;
  font-weight: bold;
}

.actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 16px; /* 假设字体大小为 16px */
}

.welcome-message {
  margin-right: 10px;
  color: #409EFF;
  font-size: 16px; /* 与 .el-dropdown-link 保持一致 */
}
</style>