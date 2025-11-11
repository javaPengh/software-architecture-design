import {defineStore} from 'pinia';
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
            const {token} = await request.post('user/login', {
                username: username,
                password: password,
                captcha: captcha
            });
            localStorage.setItem("token", token);
            this.token = token;
            this.isLoggedIn = true;
            this.getUserInfo();
        },
        async changePassword(oldPwd, newPwd) {
            console.log(this.uid)
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
            const {loginUser} = await request.get("user/getUserInfo")
            this.username = loginUser.username;
            this.uid = loginUser.uid;
            this.isLoggedIn = true;
            if (loginUser.type === "admin") {
                this.isAdmin = true;
            }
        },
    },
    persist: {
        key: 'userStore', //存储名称
        storage: localStorage, // 存储方式
        paths: ['username','uid','isAdmin'], 
    },
});