<template>
  <div class="login-container">
    <div class="title-container">
      <h1>用户登录</h1>
    </div>
    <!-- 添加 keyup.enter.native 监听 -->
    <el-form
        :model="form"
        status-icon
        :rules="rules"
        ref="loginFormRef"
        lable-width="auto"
        @keyup.enter.native="handleSubmit">

      <el-form-item prop="username">
        <el-col :span="24">
          <!-- 在输入框上也添加回车监听 -->
          <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              @keyup.enter="handleSubmit">
          </el-input>
        </el-col>
      </el-form-item>

      <el-form-item prop="password">
        <el-col :span="24">
          <el-input
              type="password"
              v-model="form.password"
              placeholder="请输入密码"
              show-password
              @keyup.enter="handleSubmit">
          </el-input>
        </el-col>
      </el-form-item>

      <el-form-item prop="captcha">
        <el-row :gutter="15">
          <el-col :span="12">
            <el-input
                v-model="form.captcha"
                placeholder="请输入验证码"
                @keyup.enter="handleSubmit">
            </el-input>
          </el-col>
          <el-col :span="12">
            <Captcha ref="captchaRef"></Captcha>
          </el-col>
        </el-row>
      </el-form-item>

      <el-row justify="center">
        <el-form-item>
          <el-col :span="24">
            <el-button
                type="primary"
                class="login-button"
                @click="handleSubmit">
              登录
            </el-button>
          </el-col>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'; // 新增生命周期钩子
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/userStore.js';
import Captcha from './Captcha.vue';

const router = useRouter();
const userStore = useUserStore();
const captchaRef = ref(null);

const form = reactive({
  username: '',
  password: '',
  captcha: ''
});

const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'change' }],
  password: [{ required: true, message: '请输入密码', trigger: 'change' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'change' }],
});

// 新增：全局回车键监听
const handleKeyPress = (event) => {
  if (event.key === 'Enter') {
    handleSubmit();
  }
};

// 新增：组件挂载时添加监听
onMounted(() => {
  window.addEventListener('keydown', handleKeyPress);
});

// 新增：组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyPress);
});

const handleSubmit = async () => {
  // 表单验证逻辑保持不变
  if (!form.captcha) {
    ElMessage.error('验证码不能为空');
    return;
  }
  try {
    await userStore.login(form.username, form.password, form.captcha);
    ElMessage.success('登录成功');
    await router.push('/home');
  } catch (error) {
    captchaRef.value?.fetchCaptcha();
  }
};
</script>

<style scoped>
.title-container {
  text-align: center;
  /* 让标题居中 */
}

.login-container {
  max-width: 400px;
  padding: 2rem;
  background-color: #f5f5f5;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin: 2rem auto 0;
}

.login-button {
  margin-top: 1rem;
}

</style>