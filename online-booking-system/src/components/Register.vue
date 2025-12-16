<template>
  <el-form ref="registerFormRef" :model="form" :rules="rules" class="register-container">
    <h2 class="register-heading">用户注册</h2>
    <el-form-item prop="username">
      <el-input v-model="form.username" placeholder="用户名" clearable></el-input>
    </el-form-item>
    <el-form-item prop="phone">
      <el-input v-model="form.phone" placeholder="电话号码" clearable></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="form.password" type="password" placeholder="密码" show-password clearable></el-input>
    </el-form-item>
    <el-form-item prop="confirmPassword">
      <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" show-password clearable></el-input>
    </el-form-item>
    <div style="text-align: center">
      <el-button type="primary" @click="handleSubmit">注册</el-button>
    </div>
  </el-form>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton } from 'element-plus';
import { useRouter } from 'vue-router';
import request from '../axios/axios.js';

const router = useRouter();

const registerFormRef = ref(null);
const form = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
});

// 定义验证函数
const validateUsername = async (rule, value, callback) => {
  const regex = /^[a-zA-Z0-9]{6,20}$/;
  if (!regex.test(value)) {
    callback(new Error('用户名必须包含字母和数字，长度为6到20个字符'));
  } else {
    let username=value;
    try {
      await request.post(`user/checkUsername/${username}`);
      callback();
    } catch (error) {
      if (typeof error === 'string' && error.includes('用户名重复')) {
        callback(new Error('用户名已存在，请选择其他用户名'));
      } else {
        callback(new Error('服务不可用，请稍后再试'));
      }
    }
  }
};

const validatePhone = (rule, value, callback) => {
  const regex = /^1[3-9]\d{9}$/;
  if (!regex.test(value)) {
    callback(new Error('请输入有效的11位电话号码'));
  } else {
    callback();
  }
};

const validatePassword = (rule, value, callback) => {
  const regex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$/;
  if (!regex.test(value)) {
    callback(new Error('密码必须包含大写字母、小写字母和数字，长度为8到16个字符'));
  } else {
    callback();
  }
};

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致，请重新输入'));
  } else {
    callback();
  }
};

// 表单验证规则
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { validator: validateUsername, trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
});

const handleSubmit = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await request.post('user/register', form);
        ElMessage.success('注册成功, 快去登录吧！');
        await router.push('/login');
      } catch (error) {
        ElMessage.error('注册失败，请检查您的信息是否正确。');
      }
    } else {
      ElMessage.error('请填写完整的信息。');
    }
  });
};

onMounted(() => {
  // 初始化操作
});
</script>

<style scoped>
.register-container {
  max-width: 400px;
  padding: 2rem;
  background-color: #f5f5f5;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin: 2rem auto 0;
}

.register-heading {
  text-align: center;
  margin-bottom: 1rem;
}
</style>
