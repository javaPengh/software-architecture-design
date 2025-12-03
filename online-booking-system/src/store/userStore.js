import { defineStore } from 'pinia';
import request from '../axios/axios.js';

export const useUserStore = defineStore('user', {
    state: () => ({
        isLoggedIn: false,
        isAdmin: false,
        uid: null,
        username: '',
        token: localStorage.getItem("token") || ''
    }),
    actions: {
        async login(username, password, captcha) {
            // --- 关键修改 1 ---
            // 从后端一次性解构出 token, role, nickname
            const { token, role, nickname } = await request.post('user/login', {
                username: username,
                password: password,
                captcha: captcha
            });

            // 保存 token
            localStorage.setItem("token", token);
            this.token = token;
            this.isLoggedIn = true;

            // --- 关键修改 2 ---
            // 直接使用登录返回的信息更新 state，不再需要单独调用 getUserInfo
            this.username = nickname; // 后端返回的 nickname 对应这里的 username

            if (role === "admin") {
                this.isAdmin = true;
            } else {
                this.isAdmin = false;
            }

            // 注意：登录接口没有返回 uid，如果其他地方需要立即使用 uid，
            // 还是需要调用 getUserInfo。但为了跳转，当前信息已足够。
            // 为了保持数据完整，我们仍然可以调用它，但不是必须的了。
            await this.getUserInfo();
        },
        async changePassword(oldPwd, newPwd) {
            const data = {
                uid: this.uid,
                oldPwd: oldPwd,
                newPwd: newPwd
            };
            return await request.post("user/changePassword", data);
        },
        logout() {
            localStorage.removeItem("token");
            this.$reset();
        },
        async getUserInfo() {
            // 这个函数现在主要用于刷新页面后恢复用户信息
            const { loginUser } = await request.get("user/getUserInfo");
            this.username = loginUser.nickname; // 确保这里用的是 nickname
            this.uid = loginUser.uid;
            this.isLoggedIn = true;
            if (loginUser.type === "admin") {
                this.isAdmin = true;
            } else {
                this.isAdmin = false;
            }
        },
    },
    persist: {
        key: 'userStore',
        storage: localStorage,
        paths: ['username', 'uid', 'isAdmin', 'isLoggedIn'], // 建议把 isLoggedIn 也持久化
    },
});