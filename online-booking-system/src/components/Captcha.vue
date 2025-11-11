<template>
  <div class="captcha-container">
    <!-- 显示验证码图片 -->
    <img :src="captchaImageUrl" alt="Captcha" @click="fetchCaptcha" />
  </div>
</template>

<script>
import request from '../axios/axios.js';

export default {
  data() {
    return {
      captchaImageUrl: '',
    };
  },
  methods: {
    async fetchCaptcha() {
      try {
        const response = await request.get('user/captcha?' + Math.random(), {
          responseType: 'blob'
        });
        // console.log(response);
        this.captchaImageUrl = URL.createObjectURL(new Blob([response.data], { type: 'image/png' }));

      } catch (error) {
        console.error('Failed to fetch captcha:', error);
      }
    }
  },
  mounted() {
    this.fetchCaptcha();
  }
};
</script>

<style scoped>
.captcha-container {
  display: inline-block;
  width: 100%;
  /* 确保与输入框宽度相同 */
}
</style>