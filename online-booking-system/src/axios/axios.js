import axios from 'axios'
import {useUserStore} from "../store/userStore.js";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import {ElMessage} from "element-plus";
//  创建instance实例
const instance = axios.create({
    baseURL:'/api/'
    // baseURL: 'http://localhost:8080/'
})
window.captcha = null; // 初始化为空
var captcha = '';
//添加请求拦截
instance.interceptors.request.use(
    // 设置请求头配置信息
    (config) => {
        NProgress.start()//开启进度条
        // 如果有token, 通过请求头携带给后台
        const userInfoStore = useUserStore()
        const token = userInfoStore.token
        if (token) {
            (config.headers)['token'] = token
        }
        //如果验证码不为空，则添加到请求头，以便后台校验验证码是否正确
        if (captcha !== ''){
            (config.headers)['Captcha'] = captcha
        }
        return config;
    },
    // 设置请求错误处理函数
    (error)=>{
        return Promise.reject(error)
    }
)
// 添加响应拦截器
instance.interceptors.response.use(
    (response) => {
        NProgress.done()//关闭进度条
        // 判断请求 是否为获取验证码请求，如果是则将获取的验证码对应的文本保存到全局变量captcha
        const headers = response.headers;
        // 1. 优先放行验证码 Header 逻辑 (解决大小写兼容)
        if (headers.captcha || headers.Captcha) {
            captcha = headers.captcha || headers.Captcha;
            return response; // 验证码接口返回完整 response
        }

        // 2. 【核心新增】如果是 Blob 二进制流，直接返回 response
        // 因为 Blob 对象没有 .code 属性，必须在业务逻辑判断前拦截
        if (response.data instanceof Blob) {
            return response;
        }
        if(response.data.code !== 200){
            // 判断响应状态码
            if (response.data.code === 501)  return  Promise.reject(ElMessage.error("用户名有误"))
            else if (response.data.code === 503) return  Promise.reject(ElMessage.error("密码有误"))
            else if (response.data.code === 504) return  Promise.reject(ElMessage.error("登录已过期"))
            else if (response.data.code === 505) return  Promise.reject(ElMessage.error("用户名重复"))
            else if (response.data.code === 506) return  Promise.reject(ElMessage.error("购票失败"))
            else if (response.data.code === 507) return  Promise.reject(ElMessage.error("订单取消失败"))
            else if (response.data.code === 508) return  Promise.reject(ElMessage.error("更新失败"))
            else if (response.data.code === 509) return  Promise.reject(ElMessage.error("删除失败"))
            else if (response.data.code === 510) return  Promise.reject(ElMessage.error("验证码有误"))
            else if (response.data.code === 511) return  Promise.reject(ElMessage.error("放映场次时间冲突"))
            else if (response.data.code === 513) return  Promise.reject(ElMessage.error("座位已被占用，请选择其他座位"))
        } else {
            // 只有当响应成功时才返回数据
            return response.data.data;
        }
    },
    (error) => {
        NProgress.done()//关闭进度条
        return Promise.reject(error.message);
    }
)
//默认导出
export default instance
